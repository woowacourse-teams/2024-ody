package com.woowacourse.ody.data.remote.ody.service

import com.woowacourse.ody.data.remote.ody.entity.notification.response.NotificationLogsResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationService {
    @GET(NOTIFICATION_LOG_PATH)
    suspend fun fetchNotificationLogs(
        @Path("meetingId") meetingId: Long,
    ): NotificationLogsResponse

    companion object {
        const val NOTIFICATION_LOG_PATH = "/meetings/{meetingId}/noti-log"
    }
}
