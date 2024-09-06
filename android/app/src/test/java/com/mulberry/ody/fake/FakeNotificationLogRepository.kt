package com.mulberry.ody.fake

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.model.NotificationLog
import com.mulberry.ody.domain.repository.ody.NotificationLogRepository
import com.mulberry.ody.notificationLogs

object FakeNotificationLogRepository : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>> {
        return ApiResult.Success(notificationLogs)
    }
}
