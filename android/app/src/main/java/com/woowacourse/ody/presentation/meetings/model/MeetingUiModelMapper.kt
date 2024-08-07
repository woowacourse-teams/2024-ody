package com.woowacourse.ody.presentation.meetings.model

import com.woowacourse.ody.domain.model.MeetingCatalog
import java.time.LocalDateTime

fun MeetingCatalog.toMeetingCatalog(): MeetingUiModel {
    val now = LocalDateTime.now()
    val inDuration = datetime.isBefore(now.plusMinutes(30))
    return MeetingUiModel(
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toString(),
        id = id,
        originAddress = originAddress,
        targetAddress = targetAddress,
        isEnabled = inDuration,
    )
}

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingUiModel> =
    mapIndexed { index, catalog ->
        catalog.toMeetingCatalog().copy(
            position = index,
        )
    }
