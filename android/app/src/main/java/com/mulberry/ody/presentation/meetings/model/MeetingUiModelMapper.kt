package com.mulberry.ody.presentation.meetings.model

import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.presentation.common.toDurationTimeMessage

private fun MeetingCatalog.toMeetingUiModel(): MeetingUiModel {
    return MeetingUiModel(
        id = id,
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toInt().toDurationTimeMessage(),
        originAddress = originAddress,
        targetAddress = targetAddress,
    )
}

fun List<MeetingCatalog>.toMeetingUiModels(): List<MeetingUiModel> {
    return map { it.toMeetingUiModel() }
}
