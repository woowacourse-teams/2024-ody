package com.mulberry.ody.data.remote.thirdparty.address.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Document(
    @Json(name = "id")
    val id: String,
    @Json(name = "place_name")
    val placeName: String,
    @Json(name = "category_name")
    val categoryName: String,
    @Json(name = "category_group_code")
    val categoryGroupCode: String,
    @Json(name = "category_group_name")
    val categoryGroupName: String,
    @Json(name = "phone")
    val phone: String,
    @Json(name = "address_name")
    val addressName: String,
    @Json(name = "road_address_name")
    val roadAddressName: String,
    @Json(name = "x")
    val x: String,
    @Json(name = "y")
    val y: String,
    @Json(name = "place_url")
    val placeUrl: String,
    @Json(name = "distance")
    val distance: String,
)
