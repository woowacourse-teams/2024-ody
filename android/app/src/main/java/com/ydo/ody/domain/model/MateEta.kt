package com.ydo.ody.domain.model

data class MateEta(
    val mateId: Long,
    val nickname: String,
    val etaType: EtaType,
    val durationMinute: Int,
)
