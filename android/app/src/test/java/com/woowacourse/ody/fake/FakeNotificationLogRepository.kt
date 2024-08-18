package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.apiresult.ApiResult
import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.notificationLogs

object FakeNotificationLogRepository : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): ApiResult<List<NotificationLog>> {
        return ApiResult.Success(notificationLogs)
    }
}
