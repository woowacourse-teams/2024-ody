package com.woowacourse.ody.data.model.notification

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationLogEntity(
    @Json(name = "type")
    val type: String,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "createdAt")
    val createdAt: String,
)
