package com.totallytot.services

import com.atlassian.confluence.schedule.ScheduledJobKey

interface PluginJobService {

    ScheduledJobKey getScheduledJobKey(String jobKey)

    void runJob(String jobKey)

    void enableJob(String jobKey)

    void disableJob(String jobKey)

    String getCronExpression(String jobKey)

    String getJobStatus(String jobKey)

}
