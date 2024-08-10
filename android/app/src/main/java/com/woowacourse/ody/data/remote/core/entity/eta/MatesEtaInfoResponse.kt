package com.woowacourse.ody.data.remote.core.entity.eta

import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.MateEta

@JsonClass(generateAdapter = true)
data class MatesEtaInfoResponse(
    val userNickname: String,
    val mateEtas: List<MateEta>,
)
