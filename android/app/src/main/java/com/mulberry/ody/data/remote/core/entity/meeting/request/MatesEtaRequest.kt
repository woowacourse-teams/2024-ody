package com.mulberry.ody.data.remote.core.entity.meeting.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MatesEtaRequest(
    @SerialName("isMissing")
    val isMissing: Boolean,
    @SerialName("currentLatitude")
    val currentLatitude: String,
    @SerialName("currentLongitude")
    val currentLongitude: String,
)
