package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.NudgeRequest
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge

fun MeetingCreationInfo.toMeetingRequest(): MeetingRequest =
    MeetingRequest(
        name = name.name,
        date = dateTime.dateTime.toLocalDate().toString(),
        time = dateTime.dateTime.toLocalTime().toString(),
        targetPlaceName = destinationAddress.toAddressString(),
        targetLatitude = destinationAddress.latitude,
        targetLongitude = destinationAddress.longitude,
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
