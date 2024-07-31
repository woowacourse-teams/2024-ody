package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.toNotificationList
import com.woowacourse.ody.data.remote.core.RetrofitClient
import com.woowacourse.ody.data.remote.core.entity.notification.response.toNotificationList
import com.woowacourse.ody.data.remote.core.service.NotificationService
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository

class DefaultNotificationLogRepository(private val notificationService: NotificationService) : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): List<NotificationLog> =
        notificationService.fetchNotificationLogs(meetingId).toNotificationList()
}
