package com.totallytot.services

import com.atlassian.confluence.schedule.ScheduledJobKey
import com.atlassian.confluence.schedule.ScheduledJobStatus
import com.atlassian.confluence.schedule.managers.ScheduledJobManager
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.spring.container.ContainerManager

import javax.inject.Named

@ExportAsService([PluginJobService])
@Named("pluginJobService")
class PluginJobServiceImpl implements PluginJobService {

    private final ScheduledJobManager scheduledJobManager

    PluginJobServiceImpl() {
        this.scheduledJobManager = (ScheduledJobManager) ContainerManager.getInstance().getComponent("scheduledJobManager")
    }

    @Override
    ScheduledJobStatus getJobStatus(String jobKey) { scheduledJobManager.scheduledJobs.find { it.key.jobId == jobKey } }

    @Override
    ScheduledJobKey getScheduledJobKey(String jobKey) { getJobStatus(jobKey).key }

    @Override
    void runJob(String jobKey) { scheduledJobManager.runNow(getScheduledJobKey(jobKey)) }

    @Override
    void enableJob(String jobKey) { scheduledJobManager.enable(getScheduledJobKey(jobKey)) }

    @Override
    void disableJob(String jobKey) { scheduledJobManager.disable(getScheduledJobKey(jobKey)) }

    @Override
    String getCronExpression(String jobKey) { scheduledJobManager.getCronExpression(getScheduledJobKey(jobKey)) }

    @Override
    Date updateCronExpression(String jobKey, String cron) {
        scheduledJobManager.updateCronJobSchedule(getScheduledJobKey(jobKey), cron)
    }

    @Override
    long getAverageRunningTime(String jobKey) { getJobStatus(jobKey).averageRunningTime }

    @Override
    Date getLastExecution(String jobKey) { getJobStatus(jobKey).lastExecution }

    @Override
    Date getNextExecution(String jobKey) { getJobStatus(jobKey).nextExecution }
}