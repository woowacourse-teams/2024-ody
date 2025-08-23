package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("mateCount")
    val mateCount: Int,
    @SerialName("date")
    val date: String,
    @SerialName("time")
    val time: String,
    @SerialName("targetAddress")
    val targetAddress: String,
    @SerialName("originAddress")
    val originAddress: String,
    @SerialName("durationMinutes")
    val durationMinutes: Long,
)
