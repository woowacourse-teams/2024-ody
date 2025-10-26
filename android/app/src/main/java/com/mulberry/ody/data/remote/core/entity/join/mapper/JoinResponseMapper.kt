package com.mulberry.ody.data.remote.core.entity.join.mapper

import com.mulberry.ody.data.remote.core.entity.join.response.JoinResponse
import com.mulberry.ody.data.util.convertLocalDateTime
import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MeetingDateTime

fun JoinResponse.toEtaOpenInfo(): EtaOpenInfo {
    return EtaOpenInfo(
        meetingId,
        MeetingDateTime(convertLocalDateTime(date, time)),
    )
}
