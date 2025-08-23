package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Document(
    @SerialName("id")
    val id: String,
    @SerialName("place_name")
    val placeName: String,
    @SerialName("address_name")
    val addressName: String,
    @SerialName("road_address_name")
    val roadAddressName: String,
    @SerialName("x")
    val x: String,
    @SerialName("y")
    val y: String,
)
