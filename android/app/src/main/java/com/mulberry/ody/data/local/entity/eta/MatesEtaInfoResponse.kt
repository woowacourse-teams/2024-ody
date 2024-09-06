package com.mulberry.ody.data.local.entity.eta

import com.mulberry.ody.domain.model.MateEta
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatesEtaInfoResponse(
    val userId: Long,
    val mateEtas: List<MateEta>,
)
