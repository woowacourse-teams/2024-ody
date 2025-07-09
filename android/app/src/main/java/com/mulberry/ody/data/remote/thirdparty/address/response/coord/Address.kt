package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("address_name")
    val addressName: String,
)
