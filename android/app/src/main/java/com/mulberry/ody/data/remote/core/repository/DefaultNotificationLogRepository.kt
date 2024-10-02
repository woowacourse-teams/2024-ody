package com.mulberry.ody.data.remote.core.repository

import com.mulberry.ody.data.remote.core.entity.notification.mapper.toNotificationList
import com.mulberry.ody.data.remote.core.service.NotificationService
import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.apiresult.map
import com.mulberry.ody.domain.model.NotificationLog
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import javax.inject.Inject

class DefaultNotificationLogRepository
    @Inject
    constructor(private val notificationService: NotificationService) :
    NotificationLogRepository {
        override suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>> =
            notificationService.fetchNotificationLogs(meetingId).map { it.toNotificationList() }
    }
