package com.woowacourse.ody.domain.model

data class Meeting(
    val id: Long,
    val name: String,
    val targetPosition: String,
    val meetingDate: String,
    val meetingTime: String,
    val mates: List<Mate>,
    val inviteCode: String,
)
