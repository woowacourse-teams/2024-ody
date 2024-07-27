package com.woowacourse.ody.data.remote.ody.entity.notification.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogsResponse(
    @Json(name = "notiLog")
    val logList: List<NotificationLogResponse>,
)
