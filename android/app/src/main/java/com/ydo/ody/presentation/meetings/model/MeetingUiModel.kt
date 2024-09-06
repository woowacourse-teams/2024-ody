package com.ydo.ody.presentation.meetings.model

import java.time.LocalDateTime

data class MeetingUiModel(
    val id: Long,
    val position: Int = 0,
    val name: String,
    val datetime: LocalDateTime,
    val originAddress: String,
    val targetAddress: String,
    val durationMinutes: String,
    val isFolded: Boolean = true,
    val isEnabled: Boolean = true,
)
