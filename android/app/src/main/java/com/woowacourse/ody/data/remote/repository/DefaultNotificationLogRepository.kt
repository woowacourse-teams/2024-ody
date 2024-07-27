package com.woowacourse.ody.data.remote.repository

import com.woowacourse.ody.data.remote.entity.toNotificationList
import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.service.NotificationService
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.NotificationLogRepository

object DefaultNotificationLogRepository : NotificationLogRepository {
    private val notificationService: NotificationService =
        RetrofitClient.retrofit.create(NotificationService::class.java)

    override suspend fun fetchNotificationLogs(meetingId: Long): List<NotificationLog> =
        notificationService.fetchNotificationLogs(meetingId).toNotificationList()
}
