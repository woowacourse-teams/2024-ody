package com.woowacourse.ody.data.remote.core.entity.meeting.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatesEtaRequest(
    @Json(name = "isMissing") val isMissing: Boolean,
    @Json(name = "currentLatitude") val currentLatitude: String,
    @Json(name = "currentLongitude") val currentLongitude: String,
)
