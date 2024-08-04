package com.woowacourse.ody.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Meeting(
    val id: Long,
    val name: String,
    val targetPosition: String,
    val meetingDate: LocalDate,
    val meetingTime: LocalTime,
    val mates: List<Mate>,
    val inviteCode: String,
)
