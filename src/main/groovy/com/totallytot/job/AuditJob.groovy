package com.totallytot.job

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem
import com.atlassian.confluence.spaces.SpaceManager
import com.atlassian.confluence.user.UserAccessor
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.transaction.TransactionCallback
import com.atlassian.scheduler.JobRunner
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import com.atlassian.sal.api.transaction.TransactionTemplate
import com.totallytot.email.AuditNotification
import com.totallytot.services.MailService
import com.totallytot.services.PluginDataService

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([AuditJob])
@Named("auditJob")
class AuditJob implements JobRunner {
    private final PluginDataService pluginDataService
    private final MailService mailService
    @ComponentImport
    private final TransactionTemplate transactionTemplate
    @ComponentImport
    private final SpaceManager spaceManager
    @ComponentImport
    private final UserAccessor userAccessor

    @Inject
    AuditJob(TransactionTemplate transactionTemplate, SpaceManager spaceManager, PluginDataService pluginDataService,
             MailService mailService, UserAccessor userAccessor) {
        this.transactionTemplate = transactionTemplate
        this.spaceManager = spaceManager
        this.pluginDataService = pluginDataService
        this.mailService = mailService
        this.userAccessor = userAccessor
    }

    @Override
    JobRunnerResponse runJob(JobRunnerRequest request) {
        if (request.isCancellationRequested()) JobRunnerResponse.aborted("Job cancelled.")
        transactionTemplate.execute(new TransactionCallback() {
            //job
            @Override
            Void doInTransaction() {
                pluginDataService.removeAllAuditReportEntities()
                runGroupPermissionsAudit()
                if (pluginDataService.emailActive) {
                    def HTML = new AuditNotification(pluginDataService.auditReportEntities).compoundHTMLNotification()
                    pluginDataService.notificationReceivers.each { userName ->
                        def mailQueueItem = new ConfluenceMailQueueItem(userAccessor.getUserByName(userName).email,
                                "Audit: groups permissions", HTML, "text/html")
                        mailService.sendEmail(mailQueueItem)
                    }
                }
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
                    permission.isGroupPermission() && monitoredGroups.contains(permission.getGroup())
                }
                if (permissionsToRemove) permissionsToRemove.each {
                    //null pointer exception related to permissions in DS space
                    def creator
                    try {
                        creator = it.creator.name
                    } catch (NullPointerException e) {
                        creator = e.message
                    }
                    pluginDataService.addRecordToAuditReport(space.key, it.group, it.type, creator, it.creationDate.toString())
                }
                if (permissionsToRemove && pluginDataService.permissionRemovalActive)
                    0.upto(permissionsToRemove.size() - 1) { space.removePermission(permissionsToRemove.get(it)) }
            }
        }
    }
}