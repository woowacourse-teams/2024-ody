package com.woowacourse.ody.data.remote.ody.repository

import com.woowacourse.ody.data.remote.RetrofitClient
import com.woowacourse.ody.data.remote.ody.entity.toNotificationList
import com.woowacourse.ody.data.remote.ody.service.NotificationService
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository

object DefaultNotificationLogRepository : NotificationLogRepository {
    private val notificationService: NotificationService =
        RetrofitClient.retrofit.create(NotificationService::class.java)

    override suspend fun fetchNotificationLogs(meetingId: Long): List<NotificationLog> =
        notificationService.fetchNotificationLogs(meetingId).toNotificationList()
}
