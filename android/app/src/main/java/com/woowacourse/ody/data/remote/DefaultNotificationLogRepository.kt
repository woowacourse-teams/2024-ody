package com.woowacourse.ody.data.remote

import com.woowacourse.ody.data.model.toNotificationList
import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationLogRepository

class DefaultNotificationLogRepository(
    private val notificationService: NotificationService,
) : NotificationLogRepository {
    override suspend fun getNotificationLogs(): List<NotificationLog> = notificationService.getNotificationLogs().toNotificationList()
}
