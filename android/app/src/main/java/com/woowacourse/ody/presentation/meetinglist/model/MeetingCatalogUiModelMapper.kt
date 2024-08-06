package com.woowacourse.ody.presentation.meetinglist.model

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

fun List<MeetingCatalog>.toMeetingCatalogUiModels(): List<MeetingCatalogUiModel> = map { it.toMeetingCatalog() }
