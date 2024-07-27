package com.woowacourse.ody.data.remote.entity.join

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JoinRequest(
    @Json(name = "inviteCode")
    val inviteCode: String,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "originAddress")
    val originAddress: String,
    @Json(name = "originLatitude")
    val originLatitude: String,
    @Json(name = "originLongitude")
    val originLongitude: String,
)
