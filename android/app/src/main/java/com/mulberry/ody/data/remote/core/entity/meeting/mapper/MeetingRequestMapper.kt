package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.mulberry.ody.data.remote.core.entity.meeting.request.NudgeRequest
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.Nudge

fun MeetingCreationInfo.toMeetingRequest(): MeetingRequest =
    MeetingRequest(
        name = name,
        date = date,
        time = time,
        targetPlaceName = targetPlaceName,
        targetLatitude = compress(targetLatitude),
        targetLongitude = compress(targetLongitude),
    )

fun Nudge.toNudgeRequest(): NudgeRequest =
    NudgeRequest(
        requestMateId = requestMateId,
        nudgedMateId = nudgedMateId,
    )

private fun compress(coordinate: String): String {
    val endIndex = minOf(9, coordinate.length)
    return coordinate.substring(0, endIndex)
}
