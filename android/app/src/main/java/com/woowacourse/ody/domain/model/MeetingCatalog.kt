package com.woowacourse.ody.domain.model

import java.time.LocalDateTime

data class MeetingCatalog(
    val durationMinutes: Int,
    val id: Int,
    val mateCount: Int,
    val name: String,
    val originAddress: String,
    val targetAddress: String,
    val datetime: LocalDateTime,
)
