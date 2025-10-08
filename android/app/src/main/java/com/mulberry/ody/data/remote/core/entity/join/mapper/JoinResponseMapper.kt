package com.mulberry.ody.data.remote.core.entity.join.mapper

import com.mulberry.ody.data.remote.core.entity.join.response.JoinResponse
import com.mulberry.ody.data.util.convertLocalDateTime
import com.mulberry.ody.domain.model.EtaOpenInfo

fun JoinResponse.toEtaOpenInfo(): EtaOpenInfo {
    return EtaOpenInfo(
        meetingId,
        convertLocalDateTime(date, time),
    )
}
