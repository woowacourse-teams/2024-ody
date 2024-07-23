package com.woowacourse.ody.data.model.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogs(
    @Json(name = "notiLog")
    val logList: List<NotificationLog>,
)
