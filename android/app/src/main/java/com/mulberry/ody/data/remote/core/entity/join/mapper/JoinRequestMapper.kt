package com.mulberry.ody.data.remote.core.entity.join.mapper

import com.mulberry.ody.data.remote.core.entity.join.request.JoinRequest
import com.mulberry.ody.domain.model.MeetingJoinInfo

fun MeetingJoinInfo.toJoinRequest(): JoinRequest =
    JoinRequest(
        inviteCode = inviteCode,
        originAddress = originAddress,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
    )
