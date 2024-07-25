package com.woowacourse.ody.domain.repository

import com.woowacourse.ody.domain.model.NotificationLog

interface NotificationLogRepository {
    suspend fun fetchNotificationLogs(meetingId: Int): List<NotificationLog>
}
