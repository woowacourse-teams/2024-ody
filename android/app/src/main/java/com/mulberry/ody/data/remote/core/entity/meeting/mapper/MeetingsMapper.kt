package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.MeetingResponse
import com.mulberry.ody.data.remote.core.entity.meeting.response.MeetingsResponse
import com.mulberry.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalTime

private fun MeetingResponse.toMeeting(): Meeting {
    val date = LocalDate.parse(date)
    val time = LocalTime.parse(time)
    val dateTime = date.atTime(time)
    return Meeting(
        durationMinutes = durationMinutes,
        id = id,
        mateCount = mateCount,
        name = name,
        originAddress = originAddress,
        targetAddress = targetAddress,
        datetime = dateTime,
    )
}

fun MeetingsResponse.toMeetings(): List<Meeting> = meetings.map { it.toMeeting() }
