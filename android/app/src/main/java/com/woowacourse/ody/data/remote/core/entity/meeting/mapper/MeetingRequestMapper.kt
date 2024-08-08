package com.woowacourse.ody.data.remote.core.entity.meeting.mapper

import com.woowacourse.ody.data.remote.core.entity.meeting.request.MeetingRequest
import com.woowacourse.ody.domain.model.MeetingCreationInfo

fun MeetingCreationInfo.toMeetingRequest(): MeetingRequest =
    MeetingRequest(
        name = name,
        date = date,
        time = time,
        targetAddress = targetAddress,
        targetLatitude = compress(targetLatitude),
        targetLongitude = compress(targetLongitude),
    )

private fun compress(coordinate: String): String {
    val endIndex = minOf(9, coordinate.length)
    return coordinate.substring(0, endIndex)
}
