package com.woowacourse.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json

data class Address(
    @Json(name = "address_name") val addressName: String,
    @Json(name = "b_code") val bCode: String,
    @Json(name = "h_code") val hCode: String,
    @Json(name = "main_address_no") val mainAddressNo: String,
    @Json(name = "mountain_yn") val mountainYn: String,
    @Json(name = "region_1depth_name") val region1depthName: String,
    @Json(name = "region_2depth_name") val region2depthName: String,
    @Json(name = "region_3depth_h_name") val region3depthHName: String,
    @Json(name = "region_3depth_name") val region3depthName: String,
    @Json(name = "sub_address_no") val subAddressNo: String,
    @Json(name = "x") val x: String,
    @Json(name = "y") val y: String,
)
