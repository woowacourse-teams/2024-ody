package com.mulberry.ody.data.remote.core.entity.meeting.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NudgeRequest(
    @SerialName("requestMateId")
    val requestMateId: Long,
    @SerialName("nudgedMateId")
    val nudgedMateId: Long,
)
