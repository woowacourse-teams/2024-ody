package com.ydo.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json

data class Document(
    @Json(name = "address") val address: Address,
    @Json(name = "address_name") val addressName: String,
    @Json(name = "address_type") val addressType: String,
    @Json(name = "road_address") val roadAddress: RoadAddress,
    @Json(name = "x") val x: String,
    @Json(name = "y") val y: String,
)
