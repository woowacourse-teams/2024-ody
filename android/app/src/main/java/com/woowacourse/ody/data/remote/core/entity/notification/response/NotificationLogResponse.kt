package com.woowacourse.ody.data.remote.core.entity.notification.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogResponse(
    @Json(name = "type")
    val type: String,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "createdAt")
    val createdAt: String,
)
