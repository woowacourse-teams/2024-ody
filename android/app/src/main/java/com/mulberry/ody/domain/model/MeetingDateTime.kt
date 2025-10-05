package com.mulberry.ody.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingDateTime(val dateTime: LocalDateTime) {
    constructor(date: LocalDate, time: LocalTime) : this(LocalDateTime.of(date, time))

    fun isValid(): Boolean {
        return dateTime.isAfter(LocalDateTime.now())
    }
}
