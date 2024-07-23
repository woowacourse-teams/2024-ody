package com.woowacourse.ody.data.remote

import com.woowacourse.ody.data.model.toNotificationList
import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationLogRepository

object DefaultNotificationLogRepository : NotificationLogRepository {
    private val notificationService: NotificationService =
        RetrofitClient.retrofit.create(NotificationService::class.java)

    override suspend fun getNotificationLogs(): List<NotificationLog> = notificationService.getNotificationLogs().toNotificationList()
}
