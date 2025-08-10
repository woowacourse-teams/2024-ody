package com.mulberry.ody.presentation.feature.room.detail.model

import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.presentation.common.toDurationTimeMessage
import com.mulberry.ody.presentation.common.toMessage

fun DetailMeeting.toDetailMeetingUiModel(): DetailMeetingUiModel {
    return DetailMeetingUiModel(
        id = id,
        name = name.name,
        dateTime = dateTime.dateTime.toMessage(),
        destinationAddress = departureAddress,
        departureAddress = destinationAddress,
        departureTime = departureTime.toMessage(),
        durationTime = durationTime.toDurationTimeMessage(),
        inviteCode = inviteCode,
    )
}
