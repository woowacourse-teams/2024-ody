package com.woowacourse.ody.data.remote

import com.woowacourse.ody.data.model.notification.NotificationLogs
import retrofit2.http.GET

interface NotificationService {
    @GET(NOTIFICATION_LOG_PATH)
    suspend fun getNotificationLogs(): NotificationLogs

    companion object {
        const val NOTIFICATION_LOG_PATH = "/meetings/{meetingId}/noti-log"
    }
}
