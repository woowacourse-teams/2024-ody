package com.woowacourse.ody.presentation.home.model

import com.woowacourse.ody.domain.model.MeetingCatalog

fun MeetingCatalog.toMeetingCatalog(): MeetingCatalogUiModel =
    MeetingCatalogUiModel(
        name = name,
        datetime = datetime,
        durationMinutes = durationMinutes.toString(),
        id = id,
        originAddress = originAddress,
        targetAddress = targetAddress,
    )

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingCatalogUiModel> =
    mapIndexed { index, catalog ->
        catalog.toMeetingCatalog().copy(
            position = index,
        )
    }
