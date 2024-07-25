package com.woowacourse.ody.domain

data class Meeting(
    val id: Long,
    val name: String,
    val targetPosition: String,
    val meetingDate: String,
    val meetingTime: String,
    val mates: List<Mate>,
)
