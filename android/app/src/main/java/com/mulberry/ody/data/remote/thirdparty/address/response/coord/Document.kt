package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Document(
    @SerialName("address")
    val address: Address?,
)
