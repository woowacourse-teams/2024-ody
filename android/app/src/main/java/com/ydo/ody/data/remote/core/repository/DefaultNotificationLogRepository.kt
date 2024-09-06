package com.ydo.ody.data.remote.core.repository

import com.ydo.ody.data.remote.core.entity.notification.mapper.toNotificationList
import com.ydo.ody.data.remote.core.service.NotificationService
import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.apiresult.map
import com.ydo.ody.domain.model.NotificationLog
import com.ydo.ody.domain.repository.ody.NotificationLogRepository

class DefaultNotificationLogRepository(private val notificationService: NotificationService) :
    NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>> =
        notificationService.fetchNotificationLogs(meetingId).map { it.toNotificationList() }
}
