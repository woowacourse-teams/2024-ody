package com.mulberry.ody.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MateEtaInfo(
    val userId: Long,
    val mateEtas: List<MateEta>,
)
