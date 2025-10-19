package com.mulberry.ody.presentation.feature.room.detail.model

import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.presentation.common.toLocalDateTime

data class DetailMeetingUiModel(
    val id: Long,
    val name: String,
    val dateTime: String,
    val destinationAddress: String,
    val departureAddress: String,
    val departureTime: String,
    val durationTime: String,
    val inviteCode: String,
) {
    fun isEtaOpenTime(): Boolean {
        val meetingDateTime = MeetingDateTime(dateTime.toLocalDateTime())
        return meetingDateTime.isEtaOpenTime()
    }

    fun isDefault(): Boolean = this == DEFAULT

    companion object {
        val DEFAULT: DetailMeetingUiModel =
            DetailMeetingUiModel(
                id = -1L,
                name = "-",
                dateTime = "0",
                destinationAddress = "-",
                departureAddress = "-",
                departureTime = "-",
                durationTime = "-",
                inviteCode = "",
            )
    }
}
