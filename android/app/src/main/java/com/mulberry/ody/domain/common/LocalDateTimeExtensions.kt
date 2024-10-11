package com.mulberry.ody.domain.common

import java.time.LocalDateTime
import java.time.ZoneId

fun LocalDateTime.toMilliSeconds(zoneId: String = "Asia/Seoul"): Long = atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
