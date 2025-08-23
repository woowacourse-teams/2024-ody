package com.mulberry.ody.data.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun convertLocalDateTime(
    date: String,
    time: String,
): LocalDateTime {
    return LocalDateTime.of(date.toLocalDate(), time.toLocalTime())
}

fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

fun String.toLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
