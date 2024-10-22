package com.mulberry.ody.domain.model

sealed interface EtaStatus {
    data object Arrived : EtaStatus

    data class ArrivalSoon(val durationMinutes: Int) : EtaStatus

    data class LateWarning(val durationMinutes: Int) : EtaStatus

    data class Late(val durationMinutes: Int) : EtaStatus

    data object Missing : EtaStatus
}
