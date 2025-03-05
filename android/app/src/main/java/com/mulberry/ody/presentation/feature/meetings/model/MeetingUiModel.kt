package com.mulberry.ody.presentation.feature.meetings.model

import java.time.LocalDateTime

data class MeetingUiModel(
    val id: Long,
    val name: String,
    val datetime: LocalDateTime,
    val originAddress: String,
    val targetAddress: String,
    val durationMinutes: String,
    val isFolded: Boolean = true,
) {
    fun isAccessible(): Boolean {
        val now = LocalDateTime.now()
        return datetime.isBefore(now.plusMinutes(30))
    }
}
