package com.mulberry.ody.domain.model

data class MeetingCreationInfo(
    val name: String,
    val date: String,
    val time: String,
    val targetPlaceName: String,
    val targetLatitude: String,
    val targetLongitude: String,
)
