package com.mulberry.ody.data.remote.core.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("type")
    val type: String,
    @SerialName("title")
    val title: String,
    @SerialName("status")
    val status: Int,
    @SerialName("detail")
    val detail: String,
    @SerialName("instance")
    val instance: String,
)
