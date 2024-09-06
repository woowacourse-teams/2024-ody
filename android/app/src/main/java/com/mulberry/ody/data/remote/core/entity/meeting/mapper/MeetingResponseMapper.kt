package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun MeetingResponse.toMeeting(): Meeting =
    Meeting(
        id = id,
        name = name,
        targetPosition = targetAddress,
        meetingDate = date.parseToLocalDate(),
        meetingTime = time.parseToLocalTime(),
        mates = mates.map { Mate(nickname = it.nickname, imageUrl = it.imageUrl) },
        inviteCode = inviteCode,
    )

private fun String.parseToLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private fun String.parseToLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
