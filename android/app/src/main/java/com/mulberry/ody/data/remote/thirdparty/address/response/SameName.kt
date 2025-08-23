package com.mulberry.ody.data.remote.thirdparty.address.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SameName(
    @Json(name = "keyword")
    val keyword: String,
    @Json(name = "region")
    val region: List<String>,
    @Json(name = "selected_region")
    val selectedRegion: String,
)
