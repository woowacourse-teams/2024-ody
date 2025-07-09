package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Document(
    @SerialName("id")
    val id: String,
    @SerialName("place_name")
    val placeName: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("category_group_code")
    val categoryGroupCode: String,
    @SerialName("category_group_name")
    val categoryGroupName: String,
    @SerialName("phone")
    val phone: String,
    @SerialName("address_name")
    val addressName: String,
    @SerialName("road_address_name")
    val roadAddressName: String,
    @SerialName("x")
    val x: String,
    @SerialName("y")
    val y: String,
    @SerialName("place_url")
    val placeUrl: String,
    @SerialName("distance")
    val distance: String,
)
