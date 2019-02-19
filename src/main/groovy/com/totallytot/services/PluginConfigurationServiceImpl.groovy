package com.totallytot.services

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.totallytot.ao.AuditReport
import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([PluginConfigurationService])
@Named("pluginConfigurationService")
class PluginConfigurationServiceImpl implements PluginConfigurationService {

    private final String AUDIT_JOB_KEY = "groupPermissionsAuditJob"
    private final PluginDataService pluginDataService
    private final PluginJobService pluginJobService

    @Inject
    PluginConfigurationServiceImpl(PluginDataService pluginDataService, PluginJobService pluginJobService) {
        this.pluginDataService = pluginDataService
        this.pluginJobService = pluginJobService
    }

    @Override
    Map<String, Object> getConfigurationData() {
        def isJobEnabled = pluginJobService.getJobStatus(AUDIT_JOB_KEY).isEnabled()
        def jobControlButton = isJobEnabled ? "Disable" : "Enable"
        def jobStatusBar = isJobEnabled ? "Scheduled" : "Disabled"
        [
                "ignoredSpaces"     : pluginDataService.ignoredSpaces,
                "monitoredGroups"   : pluginDataService.monitoredGroups,
                "email"             : pluginDataService.emailActive.toString(),
                "permission"        : pluginDataService.permissionRemovalActive.toString(),
                "receivers"         : pluginDataService.notificationReceivers,
                "jobStatusBar"      : jobStatusBar,
                "jobControlButton"  : jobControlButton,
                "cron"              : pluginJobService.getCronExpression(AUDIT_JOB_KEY),
                "averageRunningTime": pluginJobService.getAverageRunningTime(AUDIT_JOB_KEY),
                "lastExecution"     : pluginJobService.getLastExecution(AUDIT_JOB_KEY),
                "nextExecution"     : pluginJobService.getNextExecution(AUDIT_JOB_KEY)
        ]
    }

    @Override
    boolean updateConfigDataFromJSON(String jsonData) {
        def dataObject = new JsonSlurper().parseText(jsonData)
        List<String> spacesToAdd = dataObject.spacesToAdd
        if (spacesToAdd) spacesToAdd.each { pluginDataService.setIgnoredSpace(it) }
        List<String> spacesToDel = dataObject.spacesToDel
        if (spacesToDel) spacesToDel.each { pluginDataService.removeIgnoredSpace(it) }
        List<String> groupsToAdd = dataObject.groupsToAdd
        if (groupsToAdd) groupsToAdd.each { pluginDataService.setMonitoredGroup(it) }
        List<String> groupsToDel = dataObject.groupsToDel
        if (groupsToDel) groupsToDel.each { pluginDataService.removeMonitoredGroup(it) }
        List<String> userNamesToAdd = dataObject.userNamesToAdd
        if (userNamesToAdd) userNamesToAdd.each { pluginDataService.setNotificationReceiver(it) }
        List<String> userNamesToDel = dataObject.userNamesToDel
        if (userNamesToDel) userNamesToDel.each { pluginDataService.removeNotificationReceiver(it) }
        def email = dataObject.email as Boolean
        if (email != null) pluginDataService.activateEmail(email)
        def permission = dataObject.permission as Boolean
        if (permission != null) pluginDataService.activatePermissionRemoval(permission)
        true
    }

    @Override
    void enableAuditJob() { pluginJobService.enableJob(AUDIT_JOB_KEY) }

    @Override
    void disableAuditJob() { pluginJobService.disableJob(AUDIT_JOB_KEY) }

    @Override
    void runAuditJob() { pluginJobService.runJob(AUDIT_JOB_KEY) }

    @Override
    List<AuditReport> getAuditReportEntities() { pluginDataService.auditReportEntities }

}
