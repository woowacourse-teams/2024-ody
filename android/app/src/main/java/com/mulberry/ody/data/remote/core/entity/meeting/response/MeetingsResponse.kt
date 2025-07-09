package com.mulberry.ody.data.remote.core.entity.meeting.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MeetingsResponse(
    @SerialName("meetings")
    val meetings: List<MeetingResponse>,
)
