package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MateEtaResponse(
    @SerialName("mateId")
    val mateId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("status")
    val status: String,
    @SerialName("durationMinutes")
    val durationMinutes: Long,
)
