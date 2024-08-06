package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeetingCatalogResponse(
    val date: String,
    val durationMinutes: Int,
    val id: Int,
    val mateCount: Int,
    val name: String,
    val originAddress: String,
    val targetAddress: String,
    val time: String,
)
