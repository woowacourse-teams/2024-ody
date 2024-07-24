package com.woowacourse.ody.data.remote.service

import com.woowacourse.ody.data.model.notification.NotificationLogsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationService {
    @GET(NOTIFICATION_LOG_PATH)
    suspend fun fetchNotificationLogs(
        @Path("meetingId") meetingId: Int,
    ): NotificationLogsResponse

    companion object {
        const val NOTIFICATION_LOG_PATH = "/meetings/{meetingId}/noti-log"
    }
}
