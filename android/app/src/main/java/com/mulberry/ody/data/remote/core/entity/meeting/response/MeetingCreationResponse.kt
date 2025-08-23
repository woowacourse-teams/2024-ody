package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingCreationResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("date")
    val date: String,
    @SerialName("time")
    val time: String,
    @SerialName("targetAddress")
    val targetAddress: String,
    @SerialName("targetLatitude")
    val targetLatitude: String,
    @SerialName("targetLongitude")
    val targetLongitude: String,
    @SerialName("inviteCode")
    val inviteCode: String,
)
