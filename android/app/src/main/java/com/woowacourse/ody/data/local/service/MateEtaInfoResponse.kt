package com.woowacourse.ody.data.local.service

import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.MateEta

@JsonClass(generateAdapter = true)
data class MateEtaInfoResponse(
    val userNickname: String,
    val mateEtas: List<MateEta>,
)
