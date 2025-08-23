package com.mulberry.ody.domain.model

data class MeetingCreationInfo(
    val name: MeetingName,
    val dateTime: MeetingDateTime,
    val destinationAddress: Address,
)
