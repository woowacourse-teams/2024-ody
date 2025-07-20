package com.mulberry.ody.data.remote.core.entity.join.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JoinRequest(
    @SerialName("inviteCode")
    val inviteCode: String,
    @SerialName("originAddress")
    val originAddress: String,
    @SerialName("originLatitude")
    val originLatitude: String,
    @SerialName("originLongitude")
    val originLongitude: String,
)
