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

    Date updateSimpleJobSchedule(String jobKey, long repeatInterval)

}
