package com.woowacourse.ody.presentation.room.log.model

import com.woowacourse.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Meeting.toMeetingUiModel(): MeetingDetailUiModel =
    MeetingDetailUiModel(
        name,
        targetPosition,
        toMeetingDateTime(meetingDate, meetingTime),
        mates.map { it.nickname },
        inviteCode,
        meetingDate == LocalDate.now() && meetingTime.minusMinutes(30) <= LocalTime.now(),
    )

private fun toMeetingDateTime(
    meetingDate: LocalDate,
    meetingTime: LocalTime,
): String {
    val dateTime = LocalDateTime.of(meetingDate, meetingTime)
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
    return dateTime.format(dateTimeFormatter)
}
