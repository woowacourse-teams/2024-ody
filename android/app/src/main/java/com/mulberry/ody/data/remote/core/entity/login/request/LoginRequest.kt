package com.mulberry.ody.data.remote.core.entity.login.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("deviceToken")
    val deviceToken: String,
    @SerialName("providerId")
    val providerId: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("imageUrl")
    val imageUrl: String,
)
