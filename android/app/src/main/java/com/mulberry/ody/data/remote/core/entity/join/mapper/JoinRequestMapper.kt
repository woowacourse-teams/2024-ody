package com.mulberry.ody.data.remote.core.entity.join.mapper

import com.mulberry.ody.data.remote.core.entity.join.request.JoinRequest
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingJoinInfo

fun MeetingJoinInfo.toJoinRequest(): JoinRequest =
    JoinRequest(
        inviteCode = inviteCode,
        originAddress = departureAddress.toAddressString(),
        originLatitude = departureAddress.latitude,
        originLongitude = departureAddress.longitude,
    )

private fun Address.toAddressString(): String {
    if (placeName.isBlank()) return detailAddress
    return "$detailAddress ($placeName)"
}
