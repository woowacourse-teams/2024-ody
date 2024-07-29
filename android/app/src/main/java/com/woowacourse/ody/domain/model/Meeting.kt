package com.woowacourse.ody.domain.model

import com.woowacourse.ody.presentation.room.model.MeetingUiModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Meeting(
    val id: Long,
    val name: String,
    val targetPosition: String,
    val meetingDate: LocalDate,
    val meetingTime: LocalTime,
    val mates: List<Mate>,
    val inviteCode: String,
) {
    fun toMeetingUiModel(): MeetingUiModel {
        return MeetingUiModel(
            this.name,
            this.targetPosition,
            this.toMeetingDateTime(),
            this.mates.map { it.nickname },
            this.inviteCode,
        )
    }

    private fun toMeetingDateTime(): String {
        val dateTime = LocalDateTime.of(meetingDate, meetingTime)
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 HH시 mm분")
        return dateTime.format(dateTimeFormatter)
    }
}
