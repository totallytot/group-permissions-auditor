package com.totallytot.servlet

import com.atlassian.templaterenderer.TemplateRenderer
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.spaces.SpaceStatus
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.plugin.spring.scanner.annotation.component.Scanned
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.auth.LoginUriProvider
import com.atlassian.sal.api.user.UserManager
import com.totallytot.services.PluginConfigurationService

import javax.inject.Inject
import javax.servlet.ServletException
import javax.servlet.http.HttpServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.stream.Collectors

@Scanned
class Configuration extends HttpServlet {
    private final PluginConfigurationService pluginConfigurationService
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

    @Inject
    Configuration(LoginUriProvider loginUriProvider, UserManager userManager, UserAccessor userAccessor,
    SpaceManager spaceManager, TemplateRenderer renderer, PluginConfigurationService pluginConfigurationService) {
        this.loginUriProvider = loginUriProvider
        this.userManager = userManager
        this.userAccessor = userAccessor
        this.spaceManager = spaceManager
        this.renderer = renderer
        this.pluginConfigurationService = pluginConfigurationService
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

        //load data for context from DB
        def context = pluginConfigurationService.configurationData
        context << ["allSpaceKeys":spaceManager.getAllSpaceKeys(SpaceStatus.CURRENT),
                    "allGroups":userAccessor.groupsAsList]
        resp.setContentType("text/html;charset=utf-8")
        renderer.render("configuration.vm", context, resp.writer)
        resp.writer.close()
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonString = req.reader.lines().collect(Collectors.joining())
        if (pluginConfigurationService.updateConfigDataFromJSON(jsonString)) resp.sendError(HttpServletResponse.SC_OK)
        else resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
    }
}