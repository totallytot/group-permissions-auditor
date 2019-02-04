package com.totallytot.job

import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.transaction.TransactionCallback
import com.atlassian.scheduler.JobRunner
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.totallytot.services.PluginDataService

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([AuditJob])
@Named("auditJob")
class AuditJob implements JobRunner {

    private final PluginDataService pluginDataService

    @ComponentImport
    private final TransactionTemplate transactionTemplate

    @ComponentImport
    private final SpaceManager spaceManager

    @Inject
    AuditJob(TransactionTemplate transactionTemplate, SpaceManager spaceManager, PluginDataService pluginDataService) {
        this.transactionTemplate = transactionTemplate
        this.spaceManager = spaceManager
        this.pluginDataService = pluginDataService
    }

    @Override
    JobRunnerResponse runJob(JobRunnerRequest request) {
        if (request.isCancellationRequested()) JobRunnerResponse.aborted("Job cancelled.")
        transactionTemplate.execute(new TransactionCallback() {
            //job
            @Override
            Void doInTransaction() {
                runGroupPermissionsAudit()
                null
            }
        })
        JobRunnerResponse.success("Job finished successfully.")
    }

    void runGroupPermissionsAudit() {
        def allSpaces = spaceManager.allSpaces
        def ignoredSpaces = pluginDataService.ignoredSpaces
        def monitoredGroups = pluginDataService.monitoredGroups
        if (ignoredSpaces && monitoredGroups) {
            def spacesForAudit = allSpaces.findAll { space -> !ignoredSpaces.contains(space.key) }
            spacesForAudit.each { space ->
                def permissionsToRemove = space.getPermissions().findAll { permission ->
                    permission.isGroupPermission() && monitoredGroups.contains(permission.getGroup()) }
                if (permissionsToRemove) 0.upto(permissionsToRemove.size() - 1) { space.removePermission(permissionsToRemove.get(it)) }
            }
        }
    }
}