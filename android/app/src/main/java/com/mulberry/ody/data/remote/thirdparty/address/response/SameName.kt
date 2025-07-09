package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SameName(
    @SerialName("keyword")
    val keyword: String,
    @SerialName("region")
    val region: List<String>,
    @SerialName("selected_region")
    val selectedRegion: String,
)
