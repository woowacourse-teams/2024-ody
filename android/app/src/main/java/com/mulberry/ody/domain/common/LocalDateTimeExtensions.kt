package com.mulberry.ody.domain.common

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toMilliSeconds(zoneId: String = "Asia/Seoul"): Long {
    return atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
}

fun LocalDate.toMilliSeconds(zoneId: String = "Asia/Seoul"): Long {
    return atStartOfDay(ZoneId.of(zoneId)).toInstant().toEpochMilli()
}
