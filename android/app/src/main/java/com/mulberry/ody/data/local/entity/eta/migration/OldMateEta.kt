package com.mulberry.ody.data.local.entity.eta.migration

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OldMateEta(
    val mateId: Long,
    val nickname: String,
    val etaType: EtaType,
    val durationMinute: Int,
)
