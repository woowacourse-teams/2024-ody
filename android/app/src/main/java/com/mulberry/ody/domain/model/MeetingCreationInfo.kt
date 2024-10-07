package com.mulberry.ody.domain.model

import java.time.LocalDateTime

data class MeetingCreationInfo(
    val name: String,
    val dateTime: LocalDateTime,
    val destinationAddress: Address,
)
