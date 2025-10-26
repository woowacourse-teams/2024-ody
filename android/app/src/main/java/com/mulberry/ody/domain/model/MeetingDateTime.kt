package com.mulberry.ody.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingDateTime(val dateTime: LocalDateTime) {
    constructor(date: LocalDate, time: LocalTime) : this(LocalDateTime.of(date, time))

    fun isValid(): Boolean {
        return dateTime.isAfter(LocalDateTime.now())
    }

    fun isEtaOpenTime(): Boolean {
        return LocalDateTime.now() >= dateTime.minusMinutes(ETA_OPEN_MINUTE)
    }

    companion object {
        private const val ETA_OPEN_MINUTE = 30L
    }
}
