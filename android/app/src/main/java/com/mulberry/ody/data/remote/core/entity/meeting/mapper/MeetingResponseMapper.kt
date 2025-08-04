package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.DetailMeetingResponse
import com.mulberry.ody.data.util.convertLocalDateTime
import com.mulberry.ody.data.util.toLocalTime
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingName

fun DetailMeetingResponse.toDetailMeeting(): DetailMeeting =
    DetailMeeting(
        id = id,
        name = MeetingName(name),
        dateTime = MeetingDateTime(dateTime = convertLocalDateTime(date, time)),
        destinationAddress = targetAddress,
        departureAddress = originAddress,
        departureTime = departureTime.toLocalTime(),
        durationTime = routeTime,
        mates = mates.map { Mate(nickname = it.nickname, imageUrl = it.imageUrl) },
        inviteCode = inviteCode,
    )
