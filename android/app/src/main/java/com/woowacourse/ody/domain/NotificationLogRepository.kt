package com.woowacourse.ody.domain

interface NotificationLogRepository {
    suspend fun fetchNotificationLogs(meetingId: Int): List<NotificationLog>
}
