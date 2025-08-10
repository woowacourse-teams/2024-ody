package com.mulberry.ody.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingDateTime(val dateTime: LocalDateTime) {
    init {
        require(dateTime.isAfter(LocalDateTime.now())) { "약속 시간은 현재 시간보다 이후여야 합니다." }
    }

    constructor(date: LocalDate, time: LocalTime) : this(LocalDateTime.of(date, time))
}
