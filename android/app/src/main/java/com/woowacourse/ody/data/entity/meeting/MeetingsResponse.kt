package com.woowacourse.ody.data.entity.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeetingsResponse(
    @Json(name = "meetings")
    val meetings: List<MeetingResponse>,
)
