package com.woowacourse.ody.domain

import java.time.LocalDateTime

data class Meeting(
    val id: Int,
    val name: String,
    val targetPosition: String,
    val meetingTime: LocalDateTime,
    val mates: List<Mate>,
)
