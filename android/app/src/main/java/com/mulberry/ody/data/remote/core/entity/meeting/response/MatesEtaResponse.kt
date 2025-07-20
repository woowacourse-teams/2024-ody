package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatesEtaResponse(
    @SerialName("requesterMateId")
    val requesterMateId: Long,
    @SerialName("mateEtas")
    val matesEtaResponses: List<MateEtaResponse>,
)
