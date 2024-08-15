package com.woowacourse.ody.fake

import com.woowacourse.ody.domain.model.NotificationLog
import com.woowacourse.ody.domain.repository.ody.NotificationLogRepository
import com.woowacourse.ody.notificationLogs

object FakeNotificationLogRepository : NotificationLogRepository {
    override suspend fun fetchNotificationLogs(meetingId: Long): Result<List<NotificationLog>> {
        return Result.success(notificationLogs)
    }
}
