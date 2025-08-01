package com.mulberry.ody.domain.model

data class Meeting(
    val id: Long,
    val name: MeetingName,
    val mateCount: Int,
    val dateTime: MeetingDateTime,
    val targetAddress: String,
    val originAddress: String,
    val durationMinutes: Long,
)
