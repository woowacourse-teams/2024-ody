package com.mulberry.ody.data.remote.core.entity.join.mapper

import com.mulberry.ody.data.remote.core.entity.join.request.JoinRequest
import com.mulberry.ody.domain.model.MeetingJoinInfo

fun MeetingJoinInfo.toJoinRequest(): JoinRequest =
    JoinRequest(
        inviteCode = inviteCode,
        originPlaceName = originPlaceName,
        originLatitude = compress(originLatitude),
        originLongitude = compress(originLongitude),
    )

private fun compress(coordinate: String): String {
    val endIndex = minOf(9, coordinate.length)
    return coordinate.substring(0, endIndex)
}
