package com.totallytot.services

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import groovy.json.JsonSlurper

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([PluginConfigurationService])
@Named("pluginConfigurationService")
class PluginConfigurationServiceImpl implements PluginConfigurationService{

    private final PluginDataService pluginDataService

    @Inject
    PluginConfigurationServiceImpl(PluginDataService pluginDataService) {
        this.pluginDataService = pluginDataService
    }

    @Override
    Map<String, Object> getConfigurationData() {
        ["ignoredSpaces":pluginDataService.ignoredSpaces, "monitoredGroups":pluginDataService.monitoredGroups]
    }

    @Override
    boolean updateConfigDataFromJSON(String jsonData) {
        def dataObject = new JsonSlurper().parseText(jsonData)
        List<String> spacesToAdd = dataObject.spacesToAdd
        List<String> spacesToDel = dataObject.spacesToDel
        List<String> groupsToAdd = dataObject.groupsToAdd
        List<String> groupsToDel = dataObject.groupsToDel
        if (spacesToAdd) spacesToAdd.each {pluginDataService.setIgnoredSpace(it)}
        if (spacesToDel) spacesToDel.each {pluginDataService.removeIgnoredSpace(it)}
        if (groupsToAdd) groupsToAdd.each {pluginDataService.setMonitoredGroup(it)}
        if (groupsToDel) groupsToDel.each {pluginDataService.removeMonitoredGroup(it)}
        true
    }
}