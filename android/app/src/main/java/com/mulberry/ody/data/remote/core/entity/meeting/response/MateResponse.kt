package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MateResponse(
    @SerialName("nickname")
    val nickname: String,
    @SerialName("imageUrl")
    val imageUrl: String,
)
