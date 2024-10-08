package com.mulberry.ody.presentation.room.log.model

data class MeetingDetailUiModel(
    val name: String = "-",
    val targetPosition: String = "-",
    val meetingTime: String = "-",
    val mates: List<String> = listOf("-"),
    val inviteCode: String = "-",
    val isEtaAccessible: Boolean = false,
)
