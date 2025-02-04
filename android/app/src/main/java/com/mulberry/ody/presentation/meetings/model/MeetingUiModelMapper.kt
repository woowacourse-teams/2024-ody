package com.mulberry.ody.presentation.meetings.model

import com.mulberry.ody.domain.model.MeetingCatalog
import com.mulberry.ody.presentation.common.toDurationTimeMessage
import java.time.LocalDateTime

fun MeetingCatalog.toMeetingCatalogUiModel(): MeetingUiModel {
    val now = LocalDateTime.now()
    val inDuration = datetime.isBefore(now.plusMinutes(30))
    return MeetingUiModel(
        id = id,
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toInt().toDurationTimeMessage(),
        originAddress = originAddress,
        targetAddress = targetAddress,
        isEnabled = inDuration,
    )
}

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingUiModel> =
    mapIndexed { index, catalog ->
        catalog.toMeetingCatalogUiModel().copy(
            position = index,
        )
    }
