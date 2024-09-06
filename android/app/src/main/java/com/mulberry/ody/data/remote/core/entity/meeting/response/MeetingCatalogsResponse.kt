package com.mulberry.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MeetingCatalogsResponse(
    @Json(name = "meetings")
    val meetingCatalogs: List<MeetingCatalogResponse>,
)
