package com.woowacourse.ody.data.remote.core.entity.join.response

import com.woowacourse.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun JoinResponse.toMeeting(): Meeting {
    return Meeting(
        id = id,
        name = name,
        targetPosition = targetAddress,
        meetingDate = date.parseToLocalDate(),
        meetingTime = time.parseToLocalTime(),
        inviteCode = inviteCode,
    )
}

private fun String.parseToLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private fun String.parseToLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
