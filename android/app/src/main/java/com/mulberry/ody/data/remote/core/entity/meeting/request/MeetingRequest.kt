package com.mulberry.ody.data.remote.core.entity.meeting.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingRequest(
    @SerialName("name")
    val name: String,
    @SerialName("date")
    val date: String,
    @SerialName("time")
    val time: String,
    @SerialName("targetAddress")
    val targetPlaceName: String,
    @SerialName("targetLatitude")
    val targetLatitude: String,
    @SerialName("targetLongitude")
    val targetLongitude: String,
)
