package com.woowacourse.ody.data.model.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MateEntity(
    @Json(name = "nickname")
    val nickname: String,
)
