package com.woowacourse.ody.data.remote.core.entity.join.request

import com.woowacourse.ody.domain.model.MeetingJoinInfo

fun MeetingJoinInfo.toJoinRequest(): JoinRequest =
    JoinRequest(
        inviteCode = inviteCode,
        nickname = nickname,
        originAddress = originAddress,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
    )