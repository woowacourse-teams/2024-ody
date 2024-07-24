package com.woowacourse.ody.data.model.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogsResponse(
    @Json(name = "notiLog")
    val logList: List<NotificationLogResponse>,
)
