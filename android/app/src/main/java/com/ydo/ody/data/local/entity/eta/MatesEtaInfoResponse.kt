package com.ydo.ody.data.local.entity.eta

import com.squareup.moshi.JsonClass
import com.ydo.ody.domain.model.MateEta

@JsonClass(generateAdapter = true)
data class MatesEtaInfoResponse(
    val userId: Long,
    val mateEtas: List<MateEta>,
)
