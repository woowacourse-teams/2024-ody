package com.woowacourse.ody.data.remote

import com.woowacourse.ody.data.model.notification.NotificationLogEntities
import retrofit2.http.GET

interface NotificationService {
    @GET(NOTIFICATION_LOG_PATH)
    suspend fun getNotificationLogs(): NotificationLogEntities

    companion object {
        const val NOTIFICATION_LOG_PATH = "/meetings/{meetingId}/noti-log"
    }
}
