package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.NudgeRequest
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge

fun MeetingCreationInfo.toMeetingRequest(): MeetingRequest =
    MeetingRequest(
        name = name,
        date = dateTime.toLocalDate().toString(),
        time = dateTime.toLocalTime().toString(),
        targetPlaceName = destinationAddress.toAddressString(),
        targetLatitude = compress(destinationAddress.latitude),
        targetLongitude = compress(destinationAddress.longitude),
    )

private fun Address.toAddressString(): String {
    if (placeName.isBlank()) return detailAddress
    return "$detailAddress ($placeName)"
}

fun Nudge.toNudgeRequest(): NudgeRequest =
    NudgeRequest(
        requestMateId = requestMateId,
        nudgedMateId = nudgedMateId,
    )

private fun compress(coordinate: String): String {
    val endIndex = minOf(9, coordinate.length)
    return coordinate.substring(0, endIndex)
}
