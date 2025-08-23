package com.mulberry.ody.domain.common

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toMilliSeconds(zoneId: String = "Asia/Seoul"): Long {
    return atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
}

fun LocalDate.toMilliSeconds(zoneId: String = "Asia/Seoul"): Long {
    return atStartOfDay(ZoneId.of(zoneId)).toInstant().toEpochMilli()
}

fun Long.millisToLocalDate(zoneId: String = "Asia/Seoul"): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.of(zoneId))
        .toLocalDate()
}
