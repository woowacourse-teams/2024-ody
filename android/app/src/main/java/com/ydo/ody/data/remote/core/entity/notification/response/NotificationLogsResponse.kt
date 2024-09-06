package com.ydo.ody.data.remote.core.entity.notification.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogsResponse(
    @Json(name = "notiLog")
    val logList: List<NotificationLogResponse>,
)
