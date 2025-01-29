package com.mulberry.ody.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Meeting(
    val id: Long,
    val name: String,
    val date: LocalDate,
    val time: LocalTime,
    val destinationAddress: String,
    val departureAddress: String,
    val departureTime: LocalTime,
    val durationTime: Int,
    val mates: List<Mate>,
    val inviteCode: String,
)
