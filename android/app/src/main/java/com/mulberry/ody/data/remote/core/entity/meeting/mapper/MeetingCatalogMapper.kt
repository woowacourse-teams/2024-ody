package com.mulberry.ody.data.remote.core.entity.meeting.mapper

import com.mulberry.ody.data.remote.core.entity.meeting.response.MeetingCatalogResponse
import com.mulberry.ody.data.remote.core.entity.meeting.response.MeetingCatalogsResponse
import com.mulberry.ody.domain.model.MeetingCatalog
import java.time.LocalDate
import java.time.LocalTime

fun MeetingCatalogResponse.toMeetingCatalog(): MeetingCatalog {
    val date = LocalDate.parse(date)
    val time = LocalTime.parse(time)
    val dateTime = date.atTime(time)
    return MeetingCatalog(
        durationMinutes = durationMinutes,
        id = id,
        mateCount = mateCount,
        name = name,
        originAddress = originAddress,
        targetAddress = targetAddress,
        datetime = dateTime,
    )
}

fun MeetingCatalogsResponse.toMeetingCatalogs(): List<MeetingCatalog> = meetingCatalogs.map { it.toMeetingCatalog() }
