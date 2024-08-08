package com.woowacourse.ody.data.remote.core.entity.join.request

import com.woowacourse.ody.domain.model.MeetingJoinInfo

private val COORDINATE_RANGE = (0..8)

fun MeetingJoinInfo.toJoinRequest(): JoinRequest =
    JoinRequest(
        inviteCode = inviteCode,
        nickname = nickname,
        originAddress = originAddress,
        originLatitude = compress(originLatitude),
        originLongitude = compress(originLongitude),
    )

private fun compress(coordinate: String): String {
    return if (coordinate.length >= 9) {
        coordinate.slice(COORDINATE_RANGE)
    } else {
        coordinate
    }
}
