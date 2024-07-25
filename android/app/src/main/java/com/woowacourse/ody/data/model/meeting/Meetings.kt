package com.woowacourse.ody.data.model.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Meetings(
    @Json(name = "meetings")
    val meetings: List<MeetingResponse>
)
