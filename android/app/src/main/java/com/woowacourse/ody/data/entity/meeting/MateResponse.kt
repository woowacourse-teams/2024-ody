package com.woowacourse.ody.data.entity.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MateResponse(
    @Json(name = "nickname")
    val nickname: String,
)
