package com.mulberry.ody.presentation.meetings.model

import com.mulberry.ody.domain.model.MeetingCatalog

private fun MeetingCatalog.toMeetingUiModel(): MeetingUiModel {
    return MeetingUiModel(
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toString(),
        id = id,
        originAddress = originAddress,
        targetAddress = targetAddress,
    )
}

fun List<MeetingCatalog>.toMeetingUiModels(): List<MeetingUiModel> {
    return map { it.toMeetingUiModel() }
}
