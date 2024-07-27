package com.woowacourse.ody.data.remote.entity.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeetingRequest(
    @Json(name = "name") val name: String,
    @Json(name = "date") val date: String,
    @Json(name = "time") val time: String,
    @Json(name = "targetAddress") val targetAddress: String,
    @Json(name = "targetLatitude") val targetLatitude: String,
    @Json(name = "targetLongitude") val targetLongitude: String,
    @Json(name = "nickname") val nickname: String,
    @Json(name = "originAddress") val originAddress: String,
    @Json(name = "originLatitude") val originLatitude: String,
    @Json(name = "originLongitude") val originLongitude: String,
)
