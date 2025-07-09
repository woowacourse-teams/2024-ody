package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("is_end")
    val isEnd: Boolean,
)
