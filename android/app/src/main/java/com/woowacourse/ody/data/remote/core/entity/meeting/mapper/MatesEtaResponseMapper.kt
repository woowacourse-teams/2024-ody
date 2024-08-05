package com.woowacourse.ody.data.remote.core.entity.meeting.mapper

import com.woowacourse.ody.data.remote.core.entity.meeting.response.MateEtaResponse
import com.woowacourse.ody.data.remote.core.entity.meeting.response.MatesEtaResponse
import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.MateEta

fun MatesEtaResponse.toMateEtas(): List<MateEta> {
    return this.mateEtaResponses.map { it.toMateEta() }
}

private fun MateEtaResponse.toMateEta(): MateEta {
    return MateEta(
        nickname = nickname,
        etaType = status.toEtaType(),
        durationMinute = durationMinutes,
    )
}

private fun String.toEtaType(): EtaType {
    return when (this) {
        "LATE_WARNING" -> EtaType.LATE_WARNING
        "ARRIVAL_SOON" -> EtaType.ARRIVAL_SOON
        "ARRIVED" -> EtaType.ARRIVED
        "LATE" -> EtaType.LATE
        "MISSING" -> EtaType.MISSING
        else -> throw IllegalArgumentException("존재하지 않는 Eta Type 입니다.")
    }
}
