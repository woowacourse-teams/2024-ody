package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Meeting.toMeetingUiModel(): MeetingDetailUiModel {
    val meetingDateTime = LocalDateTime.of(meetingDate, meetingTime)

    return MeetingDetailUiModel(
        id,
        name,
        targetPosition,
        toMeetingDateTime(meetingDate, meetingTime),
        mates.map { it.nickname },
        inviteCode,
        meetingDateTime.minusMinutes(30) <= LocalDateTime.now(),
    )
}

private fun toMeetingDateTime(
    meetingDate: LocalDate,
    meetingTime: LocalTime,
): String {
    val dateTime = LocalDateTime.of(meetingDate, meetingTime)
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
    return dateTime.format(dateTimeFormatter)
}
