package com.woowacourse.ody.presentation.home.model

import com.woowacourse.ody.domain.model.MeetingCatalog
import java.time.LocalDateTime

fun MeetingCatalog.toMeetingCatalog(): MeetingCatalogUiModel =
    MeetingCatalogUiModel(
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toString(),
        id = id,
        originAddress = originAddress,
        targetAddress = targetAddress,
        isEnabled = datetime.isAfter(LocalDateTime.now()) && datetime.isBefore(LocalDateTime.now().plusDays(1)),
    )

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingCatalogUiModel> =
    mapIndexed { index, catalog ->
        catalog.toMeetingCatalog().copy(
            position = index,
        )
    }
