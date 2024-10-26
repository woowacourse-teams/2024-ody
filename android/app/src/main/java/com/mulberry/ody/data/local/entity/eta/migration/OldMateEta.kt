package com.mulberry.ody.data.local.entity.eta.migration

data class OldMateEta(
    val mateId: Long,
    val nickname: String,
    val etaType: EtaType,
    val durationMinute: Int,
)
