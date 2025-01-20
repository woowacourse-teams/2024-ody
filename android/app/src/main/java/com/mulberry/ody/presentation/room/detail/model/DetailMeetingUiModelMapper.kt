package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.domain.model.Meeting
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Meeting.toDetailMeetingUiModel(): DetailMeetingUiModel {
    val meetingDateTime = LocalDateTime.of(date, time)

    return DetailMeetingUiModel(
        id = id,
        name = name,
        dateTime = this.toDateTimeString(),
        destinationAddress = departureAddress,
        departureAddress = destinationAddress,
        departureTime = departureTime.toTimeString(),
        routeTime = routeTime.toTimeString(),
        mates = mates.map { it.nickname },
        inviteCode = inviteCode,
        isEtaAccessible = meetingDateTime.minusMinutes(30) <= LocalDateTime.now(),
    )
}

private fun Meeting.toDateTimeString(): String {
    val dateTime = LocalDateTime.of(date, time)
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
    return dateTime.format(dateTimeFormatter)
}

private fun LocalTime.toTimeString(): String {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("HH시 mm분")
    return format(dateTimeFormatter)
}

private fun Int.toTimeString(): String {
    val hour = this / 60
    val minute = this % 60
    val localTime = LocalTime.of(hour, minute)
    val formatterPattern = if (hour == 0) "mm분" else "HH시 mm분"
    val dateTimeFormatter = DateTimeFormatter.ofPattern(formatterPattern)
    return localTime.format(dateTimeFormatter)
}
