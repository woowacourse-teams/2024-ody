package com.woowacourse.ody.presentation.meetinglist.model

import java.time.LocalDateTime

data class MeetingCatalogUiModel(
    val id: Int,
    val name: String,
    val datetime: LocalDateTime,
    val originAddress: String,
    val targetAddress: String,
    val durationMinutes: String,
    val isFolded: Boolean = true,
)
