package com.totallytot.services

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem
import com.atlassian.core.task.MultiQueueTaskManager
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport

import javax.inject.Inject
import javax.inject.Named

@ExportAsService([MailService])
@Named("mailService")
class MailServiceImpl implements MailService {

    public static final String MAIL = "mail"

    @ComponentImport
    private final MultiQueueTaskManager taskManager

    @Inject
    MailServiceImpl(MultiQueueTaskManager taskManager) {
        this.taskManager = taskManager
    }

    @Override
    void sendEmail(ConfluenceMailQueueItem  mailQueueItem) {
        taskManager.addTask(MAIL, mailQueueItem)
    }
}