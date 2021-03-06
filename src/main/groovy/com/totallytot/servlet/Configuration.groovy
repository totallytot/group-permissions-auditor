package com.totallytot.servlet

import com.atlassian.templaterenderer.TemplateRenderer
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.spaces.SpaceStatus
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.auth.LoginUriProvider
import com.atlassian.sal.api.user.UserManager
import com.atlassian.webresource.api.assembler.PageBuilderService
import com.totallytot.services.PluginConfigurationService
import com.totallytot.services.PluginJobService
import groovy.json.JsonBuilder

import javax.inject.Inject
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.stream.Collectors

@Scanned
class Configuration extends HttpServlet {
    private final PluginConfigurationService pluginConfigurationService
    private final PluginJobService pluginJobService
    @ComponentImport
    private final LoginUriProvider loginUriProvider
    @ComponentImport
    private final UserManager userManager
    @ComponentImport
    private final UserAccessor userAccessor
    @ComponentImport
    private final SpaceManager spaceManager
    @ComponentImport
    private final TemplateRenderer renderer
    @ComponentImport
    private PageBuilderService pageBuilderService

    @Inject
    Configuration(LoginUriProvider loginUriProvider, UserManager userManager, UserAccessor userAccessor,
                  SpaceManager spaceManager, TemplateRenderer renderer, PluginConfigurationService pluginConfigurationService,
                  PageBuilderService pageBuilderService, PluginJobService pluginJobService) {
        this.loginUriProvider = loginUriProvider
        this.userManager = userManager
        this.userAccessor = userAccessor
        this.spaceManager = spaceManager
        this.renderer = renderer
        this.pluginConfigurationService = pluginConfigurationService
        this.pageBuilderService = pageBuilderService
        this.pluginJobService = pluginJobService
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString())
    }

    private static URI getUri(HttpServletRequest request) {
        def builder = request.requestURL
        if (request.queryString != null) {
            builder.append("?")
            builder.append(request.queryString)
        }
        URI.create(builder.toString())
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        def username = userManager.getRemoteUser(req)
        if (username == null || !userManager.isAdmin(username.userKey)) {
            redirectToLogin(req, resp)
            return
        }
        pageBuilderService.assembler().resources().requireWebResource("com.totallytot.group-permissions-auditor:group-permissions-auditor-resources")

        //load data for context from DB
        def context = pluginConfigurationService.configurationData
        context <<
                [
                        "allSpaceKeys": spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT),
                        "allGroups"   : userAccessor.groupsAsList,
                        "allUserNames": userAccessor.userNamesWithConfluenceAccess
                ]
        resp.setContentType("text/html;charset=utf-8")
        renderer.render("configuration.vm", context, resp.writer)
        resp.writer.close()
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.parameterNames.any { it.toString() == "disable" }) pluginConfigurationService.disableAuditJob()
        else if (req.parameterNames.any { it.toString() == "enable" }) pluginConfigurationService.enableAuditJob()
        else if (req.parameterNames.any { it.toString() == "run" }) pluginConfigurationService.runAuditJob()
        else if (req.parameterNames.any { it.toString() == "show" }) {
            def auditReportEntities = pluginConfigurationService.auditReportEntities
            def jsonBuilder = new JsonBuilder()
            jsonBuilder {
                report(auditReportEntities.collect {[
                        spacekey: it.spaceKey,
                        group: it.group,
                        permission: it.permission,
                        violator: it.violator,
                        date: it.date
                ]})
            }
            resp.setContentType("application/json")
            resp.setCharacterEncoding("UTF-8")
            resp.writer.write(jsonBuilder.toPrettyString())
            resp.writer.close()
        }
        else {
            //if no parameters are passed -> AJAX was triggered by "save" button
            Date nextExecutionDate = null
            String jsonString = req.reader.lines().collect(Collectors.joining())
            if (jsonString.contains("cron")) {
                def cron = jsonString.substring(jsonString.indexOf("cron")+7, jsonString.lastIndexOf('"'))
                nextExecutionDate = pluginConfigurationService.updateAuditJobCron(cron)
            }
            if (!pluginConfigurationService.updateConfigDataFromJSON(jsonString))
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
            if (nextExecutionDate) {
                def jsonBuilder = new JsonBuilder()
                jsonBuilder {
                    date(cron: nextExecutionDate.toString())
                }
                resp.setContentType("application/json")
                resp.setCharacterEncoding("UTF-8")
                resp.writer.write(jsonBuilder.toPrettyString())
                resp.writer.close()
            }
        }
    }
}