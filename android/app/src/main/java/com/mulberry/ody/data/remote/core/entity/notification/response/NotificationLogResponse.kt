package com.mulberry.ody.data.remote.core.entity.notification.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NotificationLogResponse(
    @SerialName("type")
    val type: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("imageUrl")
    val imageUrl: String,
)
