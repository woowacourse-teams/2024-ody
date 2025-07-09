package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    @SerialName("is_end")
    val isEnd: Boolean,
    @SerialName("pageable_count")
    val pageableCount: Int,
    @SerialName("same_name")
    val sameName: SameName,
    @SerialName("total_count")
    val totalCount: Int,
)
