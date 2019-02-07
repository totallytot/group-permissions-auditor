package com.totallytot.services

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
    String getPluginJob() {
        println("!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        scheduledJobManager.scheduledJobs.each {println(it.key.toString())}
    }
}
