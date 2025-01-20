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
        date = date.toLocalDate(),
        time = time.toLocalTime(),
        destinationAddress = targetAddress,
        departureAddress = departureAddress,
        departureTime = departureTime.toLocalTime(),
        routeTime = routeTime,
        mates = mates.map { Mate(nickname = it.nickname, imageUrl = it.imageUrl) },
        inviteCode = inviteCode,
    )

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private fun String.toLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
