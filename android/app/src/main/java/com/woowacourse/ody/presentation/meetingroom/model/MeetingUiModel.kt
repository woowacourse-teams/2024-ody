package com.woowacourse.ody.presentation.meetingroom.model

data class MeetingUiModel(
    val name: String,
    val targetPosition: String,
    val meetingTime: String,
    val mates: List<String>,
    val inviteCode: String,
)
