package com.mulberry.ody.data.remote.core.entity.meeting.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NudgeRequest(
    @Json(name = "requestMateId")
    val requestMateId: Long,
    @Json(name = "nudgedMateId")
    val nudgedMateId: Long,
)
