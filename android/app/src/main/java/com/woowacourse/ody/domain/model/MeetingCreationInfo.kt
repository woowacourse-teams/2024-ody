package com.woowacourse.ody.domain.model

data class MeetingCreationInfo(
    val name: String,
    val date: String,
    val time: String,
    val targetAddress: String,
    val targetLatitude: String,
    val targetLongitude: String,
)
