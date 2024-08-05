package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatesEtaResponse(
    @Json(name = "mateEtas")
    val mateEtaResponses: List<MateEtaResponse>
)
