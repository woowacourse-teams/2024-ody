package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Document(
    @Json(name = "address")
    val address: Address?,
    @Json(name = "road_address")
    val roadAddress: RoadAddress?
)