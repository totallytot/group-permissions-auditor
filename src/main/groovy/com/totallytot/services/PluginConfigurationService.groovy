package com.totallytot.services

interface PluginConfigurationService {

    Map<String, Object> getConfigurationData()

    boolean updateConfigDataFromJSON(String jsonData)

    void enableAuditJob()

    void disableAuditJob()
}