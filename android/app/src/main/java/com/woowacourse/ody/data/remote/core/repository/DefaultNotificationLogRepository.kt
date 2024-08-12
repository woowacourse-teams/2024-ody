package com.woowacourse.ody.data.remote.core.repository

import com.woowacourse.ody.data.remote.core.entity.notification.mapper.toNotificationList
import com.woowacourse.ody.data.remote.core.service.NotificationService
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository

class DefaultNotificationLogRepository(private val notificationService: NotificationService) : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): Result<List<NotificationLog>> =
        runCatching {
            notificationService.fetchNotificationLogs(meetingId).toNotificationList()
        }
}
