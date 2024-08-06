package com.woowacourse.ody.presentation.home.model

import java.time.LocalDateTime

data class MeetingCatalogUiModel(
    val id: Long,
    val position: Int = 0,
    val name: String,
    val datetime: LocalDateTime,
    val originAddress: String,
    val targetAddress: String,
    val durationMinutes: String,
    val isFolded: Boolean = true,
)
