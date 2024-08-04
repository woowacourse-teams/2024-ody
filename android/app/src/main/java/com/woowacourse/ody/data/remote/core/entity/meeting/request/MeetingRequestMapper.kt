package com.woowacourse.ody.data.remote.core.entity.meeting.request

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

private fun compress(coordinate: String): String = coordinate.slice(0..8)
