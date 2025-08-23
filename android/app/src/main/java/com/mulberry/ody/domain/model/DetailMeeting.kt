package com.mulberry.ody.domain.model

import java.time.LocalTime

data class DetailMeeting(
    val id: Long,
    val name: MeetingName,
    val dateTime: MeetingDateTime,
    val destinationAddress: String,
    val departureAddress: String,
    val departureTime: LocalTime,
    val durationTime: Int,
    val mates: List<Mate>,
    val inviteCode: String,
)
