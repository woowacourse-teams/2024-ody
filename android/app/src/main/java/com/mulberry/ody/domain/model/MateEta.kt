package com.mulberry.ody.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MateEta(
    val mateId: Long,
    val nickname: String,
    val etaStatus: EtaStatus,
)
