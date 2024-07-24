package com.woowacourse.ody.data.remote.repository

import com.woowacourse.ody.data.model.toNotificationList
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.NotificationService
import com.woowacourse.ody.domain.NotificationLog
import com.woowacourse.ody.domain.NotificationLogRepository

object DefaultNotificationLogRepository : NotificationLogRepository {
    private val notificationService: NotificationService =
        RetrofitClient.retrofit.create(NotificationService::class.java)

    override suspend fun fetchNotificationLogs(meetingId: Int): List<NotificationLog> =
        notificationService.fetchNotificationLogs(meetingId).toNotificationList()
}
