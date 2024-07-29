package com.woowacourse.ody.data.remote.core.entity.meeting.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.woowacourse.ody.domain.model.Meeting
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@JsonClass(generateAdapter = true)
data class MeetingResponse(
    @Json(name = "id")
    val id: Long,
    @Json(name = "name")
    val name: String,
    @Json(name = "date")
    val date: String,
    @Json(name = "time")
    val time: String,
    @Json(name = "targetAddress")
    val targetAddress: String,
    @Json(name = "targetLatitude")
    val targetLatitude: String,
    @Json(name = "targetLongitude")
    val targetLongitude: String,
    @Json(name = "mateCount")
    val mateCount: Int,
    @Json(name = "mates")
    val mates: List<MateResponse>,
    @Json(name = "inviteCode")
    val inviteCode: String,
) {
    fun toMeeting(): Meeting =
        Meeting(
            id = id,
            name = name,
            targetPosition = targetAddress,
            meetingDate = date.parseToLocalDate(),
            meetingTime = time.parseToLocalTime(),
            mates = mates.map { it.toMate() },
            inviteCode = inviteCode,
        )
}

fun String.parseToLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

fun String.parseToLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return LocalTime.parse(this, formatter)
}
