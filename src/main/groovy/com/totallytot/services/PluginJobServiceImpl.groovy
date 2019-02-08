package com.totallytot.services

import com.atlassian.confluence.schedule.ScheduledJobKey
import com.atlassian.confluence.schedule.managers.ScheduledJobManager
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.spring.container.ContainerManager

import javax.inject.Named

@ExportAsService([PluginJobService])
@Named("pluginJobService")
class PluginJobServiceImpl implements PluginJobService{

    private final ScheduledJobManager scheduledJobManager

    PluginJobServiceImpl() {
        //com.atlassian.confluence.schedule.managers -> Interface ScheduledJobManager
        this.scheduledJobManager = (ScheduledJobManager) ContainerManager.getInstance().getComponent("scheduledJobManager")
    }

    @Override
    ScheduledJobKey getScheduledJobKey(String jobKey) {
        scheduledJobManager.scheduledJobs.find {it.key.jobId == jobKey}.key
    }

    @Override
    void runJob(String jobKey) {
        scheduledJobManager.runNow(getScheduledJobKey(jobKey))
    }

    @Override
    void enableJob(String jobKey) {
        scheduledJobManager.enable(getScheduledJobKey(jobKey))
    }

    @Override
    void disableJob(String jobKey) {
        scheduledJobManager.disable(getScheduledJobKey(jobKey))
    }

    @Override
    String getCronExpression(String jobKey) {
        return null
    }

    @Override
    String getJobStatus(String jobKey) {
        scheduledJobManager.scheduledJobs.find {it.key.jobId == jobKey}.status.toString()
    }
}
