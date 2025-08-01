package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.DetailMeetingResponse
import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingName
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun DetailMeetingResponse.toDetailMeeting(): DetailMeeting =
    DetailMeeting(
        id = id,
        name = MeetingName(name),
        dateTime = convertMeetingDateTime(date, time),
        destinationAddress = targetAddress,
        departureAddress = originAddress,
        departureTime = departureTime.toLocalTime(),
        durationTime = routeTime,
        mates = mates.map { Mate(nickname = it.nickname, imageUrl = it.imageUrl) },
        inviteCode = inviteCode,
    )

private fun convertMeetingDateTime(date: String, time: String): MeetingDateTime {
    val localDateTime = LocalDateTime.of(date.toLocalDate(), time.toLocalTime())
    return MeetingDateTime(dateTime = localDateTime)
}

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private fun String.toLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
