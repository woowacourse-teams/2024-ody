package com.woowacourse.ody.domain

interface NotificationLogRepository {
    suspend fun getNotificationLogs(): List<NotificationLog>
}
