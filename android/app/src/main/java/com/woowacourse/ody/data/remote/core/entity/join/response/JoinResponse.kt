package com.woowacourse.ody.data.remote.core.entity.join.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class JoinResponse(
    @Json(name = "meetingId")
    val meetingId: Long,
    @Json(name = "date")
    val date: String,
    @Json(name = "time")
    val time: String,
)
