package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.NotificationLog

interface NotificationLogRepository {
    suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>>
}
