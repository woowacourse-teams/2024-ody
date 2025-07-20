package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DetailMeetingResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("date")
    val date: String,
    @SerialName("time")
    val time: String,
    @SerialName("originAddress")
    val originAddress: String,
    @SerialName("departureTime")
    val departureTime: String,
    @SerialName("routeTime")
    val routeTime: Int,
    @SerialName("targetAddress")
    val targetAddress: String,
    @SerialName("targetLatitude")
    val targetLatitude: String,
    @SerialName("targetLongitude")
    val targetLongitude: String,
    @SerialName("mateCount")
    val mateCount: Int,
    @SerialName("mates")
    val mates: List<MateResponse>,
    @SerialName("inviteCode")
    val inviteCode: String,
)
