package com.mulberry.ody.presentation.feature.meetings.model

import com.mulberry.ody.domain.model.Meeting
import com.mulberry.ody.presentation.common.toDurationTimeMessage

private fun Meeting.toMeetingUiModel(): MeetingUiModel {
    return MeetingUiModel(
        id = id,
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toInt().toDurationTimeMessage(),
        originAddress = originAddress,
        targetAddress = targetAddress,
    )
}

fun List<Meeting>.toMeetingUiModels(): List<MeetingUiModel> {
    return map { it.toMeetingUiModel() }
}
