package com.woowacourse.ody.presentation.home.model

import com.woowacourse.ody.domain.model.MeetingCatalog
import java.time.LocalDateTime

fun MeetingCatalog.toMeetingCatalog(): MeetingCatalogUiModel {
    val now = LocalDateTime.now()
    val inDuration = datetime.isAfter(now) && datetime.isBefore(now.plusMinutes(30))
    return MeetingCatalogUiModel(
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toString(),
        id = id,
        originAddress = originAddress,
        targetAddress = targetAddress,
        isEnabled = inDuration,
    )
}

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingCatalogUiModel> =
    mapIndexed { index, catalog ->
        catalog.toMeetingCatalog().copy(
            position = index,
        )
    }