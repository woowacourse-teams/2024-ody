package com.mulberry.ody.presentation.feature.room.detail.model

import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.presentation.common.toDurationTimeMessage
import com.mulberry.ody.presentation.common.toMessage
import java.time.LocalDateTime

fun DetailMeeting.toDetailMeetingUiModel(): DetailMeetingUiModel {
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
