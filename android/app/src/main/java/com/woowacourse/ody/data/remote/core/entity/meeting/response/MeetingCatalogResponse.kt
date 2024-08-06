package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeetingCatalogResponse(
    @Json(name = "date")
    val date: String,
    @Json(name = "durationMinutes")
    val durationMinutes: Int,
    @Json(name = "id")
    val id: Long,
    @Json(name = "mateCount")
    val mateCount: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "originAddress")
    val originAddress: String,
    @Json(name = "targetAddress")
    val targetAddress: String,
    @Json(name = "time")
    val time: String,
)
