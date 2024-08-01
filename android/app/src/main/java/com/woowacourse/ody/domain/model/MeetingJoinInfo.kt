package com.woowacourse.ody.domain.model

data class MeetingJoinInfo(
    val inviteCode: String,
    val nickname: String,
    val originAddress: String,
    val originLatitude: String,
    val originLongitude: String,
)
