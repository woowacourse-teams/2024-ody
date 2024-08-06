package com.woowacourse.ody.presentation.meetinglist.model

data class MeetingUiModel(
    val id: Int,
    val title: String,
    val datetime: String,
    val departure: String,
    val arrival: String,
    val eta: String,
    val isFolded: Boolean = true,
)
