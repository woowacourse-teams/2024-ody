package com.mulberry.ody.data.remote.thirdparty.address.response.coord

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("address_name")
    val addressName: String,
    @SerialName("region_1depth_name")
    val region1depthName: String,
    @SerialName("region_2depth_name")
    val region2depthName: String,
    @SerialName("region_3depth_name")
    val region3depthName: String,
    @SerialName("mountain_yn")
    val mountainYn: String,
    @SerialName("main_address_no")
    val mainAddressNo: String,
    @SerialName("sub_address_no")
    val subAddressNo: String,
)
