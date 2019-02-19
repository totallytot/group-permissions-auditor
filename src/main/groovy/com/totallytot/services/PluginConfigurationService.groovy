package com.totallytot.services

import com.totallytot.ao.AuditReport

interface PluginConfigurationService {

    Map<String, Object> getConfigurationData()

    boolean updateConfigDataFromJSON(String jsonData)

    void enableAuditJob()

    void disableAuditJob()

    void runAuditJob()

    List<AuditReport> getAuditReportEntities()

}