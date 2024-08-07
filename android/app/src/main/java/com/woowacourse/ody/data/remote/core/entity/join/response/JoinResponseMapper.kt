package com.woowacourse.ody.data.remote.core.entity.join.response

import com.woowacourse.ody.domain.model.ReserveInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun JoinResponse.toReserveInfo(): ReserveInfo {
    return ReserveInfo(
        meetingId,
        LocalDateTime.of(date.parseToLocalDate(), time.parseToLocalTime()),
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
