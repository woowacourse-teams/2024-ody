package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.MateEtaResponse
import com.mulberry.ody.data.remote.core.entity.meeting.response.MatesEtaResponse
import com.mulberry.ody.domain.model.EtaStatus
import com.mulberry.ody.domain.model.MateEta
import com.mulberry.ody.domain.model.MateEtaInfo

fun MatesEtaResponse.toMateEtaInfo(): MateEtaInfo {
    return MateEtaInfo(userId = requesterMateId, mateEtas = matesEtaResponses.toMateEtas())
}

fun List<MateEtaResponse>.toMateEtas(): List<MateEta> {
    return map { it.toMateEta() }
}

private fun MateEtaResponse.toMateEta(): MateEta {
    return MateEta(
        mateId = mateId,
        nickname = nickname,
        etaStatus = status.toEtaStatus(durationMinutes),
    )
}

private fun String.toEtaStatus(durationMinutes: Long): EtaStatus {
    return when (this) {
        "ARRIVED" -> EtaStatus.Arrived
        "ARRIVAL_SOON" -> EtaStatus.ArrivalSoon(durationMinutes = durationMinutes.toInt())
        "LATE_WARNING" -> EtaStatus.LateWarning(durationMinutes = durationMinutes.toInt())
        "LATE" -> EtaStatus.Late(durationMinutes = durationMinutes.toInt())
        "MISSING" -> EtaStatus.Missing
        else -> throw IllegalArgumentException("존재하지 않는 Eta Type 입니다.")
    }
}
