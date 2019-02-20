package com.totallytot.services

import com.atlassian.confluence.mail.template.ConfluenceMailQueueItem

interface MailService {

    void sendEmail(ConfluenceMailQueueItem mailQueueItem)
}