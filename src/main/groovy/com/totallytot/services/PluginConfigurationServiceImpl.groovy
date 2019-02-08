package com.totallytot.services

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([PluginConfigurationService])
@Named("pluginConfigurationService")
class PluginConfigurationServiceImpl implements PluginConfigurationService {

    private final String JOB_KEY_NAME = "groupPermissionsAuditJob"
    private final PluginDataService pluginDataService
    private final PluginJobService pluginJobService

    @Inject
    PluginConfigurationServiceImpl(PluginDataService pluginDataService, PluginJobService pluginJobService) {
        this.pluginDataService = pluginDataService
        this.pluginJobService = pluginJobService
    }

    @Override
    Map<String, Object> getConfigurationData() {
        def jobStatus = pluginJobService.getJobStatus(JOB_KEY_NAME)
        def jobControlButton = jobStatus == "SCHEDULED" ? "Disable Job" : "Enable Job"
        [
                "ignoredSpaces"    : pluginDataService.ignoredSpaces,
                "monitoredGroups"  : pluginDataService.monitoredGroups,
                "email"            : pluginDataService.emailActive.toString(),
                "permission"       : pluginDataService.permissionRemovalActive.toString(),
                "receivers"        : pluginDataService.notificationReceivers,
                "jobStatus"        : jobStatus,
                "jobControlButton" : jobControlButton
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
}
