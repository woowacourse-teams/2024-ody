package com.mulberry.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatesEtaResponse(
    @Json(name = "requesterMateId")
    val requesterMateId: Long,
    @Json(name = "mateEtas")
    val matesEtaResponses: List<MateEtaResponse>,
)
