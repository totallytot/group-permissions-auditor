package com.totallytot.job

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
import com.atlassian.sal.api.transaction.TransactionCallback
import com.atlassian.scheduler.JobRunner
import com.atlassian.scheduler.JobRunnerRequest
import com.atlassian.scheduler.JobRunnerResponse
import com.atlassian.sal.api.transaction.TransactionTemplate

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([AuditJob])
@Named("auditJob")
class AuditJob implements JobRunner{

    @ComponentImport
    private final TransactionTemplate transactionTemplate

    @Inject
    AuditJob(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate
    }

    @Override
    JobRunnerResponse runJob(JobRunnerRequest request) {
        if (request.isCancellationRequested()) {
            JobRunnerResponse.aborted("Job cancelled.")
        }

        transactionTemplate.execute(new TransactionCallback() {
            @Override
            Void doInTransaction() {
                return null
            }
        })
        JobRunnerResponse.success("Job finished successfully.")
    }
}