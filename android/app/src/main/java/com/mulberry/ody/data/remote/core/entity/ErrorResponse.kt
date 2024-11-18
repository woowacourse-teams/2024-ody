package com.mulberry.ody.data.remote.core.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "type")
    val type: String,
    @Json(name = "title")
    val title: String,
    @Json(name = "status")
    val status: Int,
    @Json(name = "detail")
    val detail: String,
    @Json(name = "instance")
    val instance: String,
)
