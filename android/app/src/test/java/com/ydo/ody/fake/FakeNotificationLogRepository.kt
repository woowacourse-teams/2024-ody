package com.ydo.ody.fake

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.NotificationLog
import com.ydo.ody.domain.repository.ody.NotificationLogRepository
import com.ydo.ody.notificationLogs

object FakeNotificationLogRepository : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>> {
        return ApiResult.Success(notificationLogs)
    }
}
