package com.totallytot.services

import com.atlassian.confluence.schedule.ScheduledJobKey
import com.atlassian.confluence.schedule.ScheduledJobStatus

interface PluginJobService {

    ScheduledJobStatus getJobStatus(String jobKey)

    ScheduledJobKey getScheduledJobKey(String jobKey)

    void runJob(String jobKey)

    void enableJob(String jobKey)

    void disableJob(String jobKey)

    String getCronExpression(String jobKey)

    Date updateCronExpression(String jobKey, String cron)

    long getAverageRunningTime(String jobKey)

    Date getLastExecution(String jobKey)

    Date getNextExecution(String jobKey)
}
