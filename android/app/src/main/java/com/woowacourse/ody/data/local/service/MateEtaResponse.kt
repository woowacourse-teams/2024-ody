package com.woowacourse.ody.data.local.service

import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.EtaType

@JsonClass(generateAdapter = true)
data class MateEtaResponse(
    val nickname: String,
    val etaType: EtaType,
    val durationMinute: Long,
)
