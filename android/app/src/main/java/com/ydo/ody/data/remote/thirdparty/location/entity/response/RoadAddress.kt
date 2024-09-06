package com.ydo.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json

data class RoadAddress(
    @Json(name = "address_name") val addressName: String,
    @Json(name = "building_name") val buildingName: String,
    @Json(name = "main_building_no") val mainBuildingNo: String,
    @Json(name = "region_1depth_name") val region1depthName: String,
    @Json(name = "region_2depth_name") val region2depthName: String,
    @Json(name = "region_3depth_name") val region3depthName: String,
    @Json(name = "road_name") val roadName: String,
    @Json(name = "sub_building_no") val subBuildingNo: String,
    @Json(name = "underground_yn") val undergroundYn: String,
    @Json(name = "x") val x: String,
    @Json(name = "y") val y: String,
    @Json(name = "zone_no") val zoneNo: String,
)
