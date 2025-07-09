package com.mulberry.ody.domain.model

import kotlinx.serialization.Serializable

@Serializable
sealed interface EtaStatus {
    @Serializable
    data object Arrived : EtaStatus

    @Serializable
    data class ArrivalSoon(val durationMinutes: Int) : EtaStatus

    @Serializable
    data class LateWarning(val durationMinutes: Int) : EtaStatus

    @Serializable
    data class Late(val durationMinutes: Int) : EtaStatus

    @Serializable
    data object Missing : EtaStatus
}
