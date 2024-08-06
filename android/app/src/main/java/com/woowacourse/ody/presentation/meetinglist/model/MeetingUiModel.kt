package com.woowacourse.ody.presentation.meetinglist.model

import java.time.LocalDateTime

data class MeetingUiModel(
    val id: Int,
    val title: String,
    val datetime: LocalDateTime,
    val departure: String,
    val arrival: String,
    val eta: String,
    val isFolded: Boolean = true,
)
