package com.mulberry.ody.data.remote.thirdparty.location.entity.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LocationSearchResponse(
    @Json(name = "documents") val documents: List<Document>,
    @Json(name = "meta") val meta: Meta,
)
