package com.ydo.ody.domain.repository.ody

import com.ydo.ody.domain.apiresult.ApiResult
import com.ydo.ody.domain.model.NotificationLog

interface NotificationLogRepository {
    suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>>
}
