package com.mulberry.ody.data.remote.core.entity.join.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinResponse(
    @SerialName("meetingId")
    val meetingId: Long,
    @SerialName("date")
    val date: String,
    @SerialName("time")
    val time: String,
)
