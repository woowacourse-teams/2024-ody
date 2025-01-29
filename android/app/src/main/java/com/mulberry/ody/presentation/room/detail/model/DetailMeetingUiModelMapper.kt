package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.presentation.common.toDurationTimeMessage
import com.mulberry.ody.presentation.common.toMessage
import java.time.LocalDateTime

fun Meeting.toDetailMeetingUiModel(): DetailMeetingUiModel {
    return DetailMeetingUiModel(
        id = id,
        name = name,
        dateTime = LocalDateTime.of(date, time).toMessage(),
        destinationAddress = departureAddress,
        departureAddress = destinationAddress,
        departureTime = departureTime.toMessage(),
        durationTime = durationTime.toDurationTimeMessage(),
        mates = mates.map { it.nickname },
        inviteCode = inviteCode,
    )
}
