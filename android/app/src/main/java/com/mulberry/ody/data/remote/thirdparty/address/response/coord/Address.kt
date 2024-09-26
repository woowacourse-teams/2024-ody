package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Address(
    @Json(name = "address_name")
    val addressName: String,
    @Json(name = "region_1depth_name")
    val region1depthName: String,
    @Json(name = "region_2depth_name")
    val region2depthName: String,
    @Json(name = "region_3depth_name")
    val region3depthName: String,
    @Json(name = "mountain_yn")
    val mountainYn: String,
    @Json(name = "main_address_no")
    val mainAddressNo: String,
    @Json(name = "sub_address_no")
    val subAddressNo: String,
)
