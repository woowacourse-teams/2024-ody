package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("total_count")
    val totalCount: Int,
)
