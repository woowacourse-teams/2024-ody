package com.woowacourse.ody.data.remote.core.entity.login.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "deviceToken")
    val deviceToken: String,
    @Json(name = "providerId")
    val providerId: String,
    @Json(name = "nickname")
    val nickname: String,
    @Json(name = "imageUrl")
    val imageUrl: String,
)
