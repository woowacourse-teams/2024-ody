package com.woowacourse.ody.domain.repository.ody

import com.woowacourse.ody.domain.model.NotificationLog

interface NotificationLogRepository {
    suspend fun fetchNotificationLogs(meetingId: Long): List<NotificationLog>
}
