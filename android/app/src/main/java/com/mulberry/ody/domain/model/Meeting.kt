package com.mulberry.ody.domain.model

import java.time.LocalDateTime

data class Meeting(
    val id: Long,
    val name: String,
    val mateCount: Int,
    val datetime: LocalDateTime,
    val targetAddress: String,
    val originAddress: String,
    val durationMinutes: Long,
)
