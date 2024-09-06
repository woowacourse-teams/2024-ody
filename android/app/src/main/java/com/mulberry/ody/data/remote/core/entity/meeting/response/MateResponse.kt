package com.mulberry.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MateResponse(
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
)
