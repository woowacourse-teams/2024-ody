package com.mulberry.ody.data.remote.core.entity.join.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JoinRequest(
    @Json(name = "inviteCode")
    val inviteCode: String,
    @Json(name = "originAddress")
    val originPlaceName: String,
    @Json(name = "originLatitude")
    val originLatitude: String,
    @Json(name = "originLongitude")
    val originLongitude: String,
)
