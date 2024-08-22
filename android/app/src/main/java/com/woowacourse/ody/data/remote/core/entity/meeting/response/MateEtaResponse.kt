package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MateEtaResponse(
    @Json(name = "mateId")
    val mateId: Long,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "status")
    val status: String,
    @Json(name = "durationMinutes")
    val durationMinutes: Long,
)
