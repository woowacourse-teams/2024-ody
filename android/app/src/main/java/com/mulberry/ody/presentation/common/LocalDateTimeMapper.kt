package com.mulberry.ody.presentation.common

import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.TemporalAccessor
import java.util.Locale

private const val DATE_TIME_PATTERN = "yyyy년 M월 d일 a h시 m분"
private const val DATE_PATTERN = "yyyy년 M월 d일"
private const val TIME_PATTERN = "a h시 m분"

private const val HOUR_MINUTE_PATTERN = "h시간 m분"
private const val MINUTE_PATTERN = "m분"

fun LocalDateTime.toMessage(): String = format(DATE_TIME_PATTERN)

fun LocalDate.toMessage(): String = format(DATE_PATTERN)

fun LocalTime.toMessage(): String = format(TIME_PATTERN)

fun Int.toDurationTimeMessage(): String {
    val hour = this / 60
    val minute = this % 60
    val localTime = LocalTime.of(hour, minute)
    val pattern = if (hour == 0) MINUTE_PATTERN else HOUR_MINUTE_PATTERN
    return localTime.format(pattern)
}

private fun TemporalAccessor.format(pattern: String): String {
    return DateTimeFormatter.ofPattern(pattern)
        .withLocale(Locale.forLanguageTag("ko"))
        .format(this)
}

fun String.toLocalDateTime(): LocalDateTime {
    val formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN, Locale.KOREAN)
    return try {
        LocalDateTime.parse(this, formatter)
    } catch (e: DateTimeParseException) {
        throw IllegalArgumentException("LocalDateTime으로 변환할 수 없는 문자열입니다. ($this)")
    }
}
