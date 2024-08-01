package com.woowacourse.ody.data.remote.core.entity.meeting.request

import com.woowacourse.ody.domain.model.MeetingCreationInfo

fun MeetingCreationInfo.toMeetingRequest(): MeetingRequest =
    MeetingRequest(
        name = name,
        date = date,
        time = time,
        targetAddress = targetAddress,
        targetLatitude = targetLatitude,
        targetLongitude = targetLongitude,
        nickname = nickname,
        originAddress = originAddress,
        originLatitude = originLatitude,
        originLongitude = originLongitude,
    )
