package com.woowacourse.ody.data.model.meeting

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalTime

@JsonClass(generateAdapter = true)
data class MeetingResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "date")
    val date: LocalDate,
    @Json(name = "time")
    val time: LocalTime,
    @Json(name = "targetAddress")
    val targetAddress: String,
    @Json(name = "targetLatitude")
    val targetLatitude: String,
    @Json(name = "targetLongtitude")
    val targetLongtitude: String,
    @Json(name = "mateCount")
    val mateCount: Int,
    @Json(name = "mates")
    val mates: List<MateResponse>,
    @Json(name = "inviteCode")
    val inviteCode: String,
)
