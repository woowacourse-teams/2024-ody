package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.presentation.common.toLocalDateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class DetailMeetingUiModel(
    val id: Long,
    val name: String,
    val dateTime: String,
    val destinationAddress: String,
    val departureAddress: String,
    val departureTime: String,
    val routeTime: String,
    val mates: List<String>,
    val inviteCode: String,
) {
    fun isEtaAccessible(): Boolean {
        val localDateTime = dateTime.toLocalDateTime()
        return localDateTime.minusMinutes(ETA_ACCESSIBLE_MINUTE) <= LocalDateTime.now()
    }

    fun isDefault(): Boolean = this == DEFAULT

    companion object {
        private const val ETA_ACCESSIBLE_MINUTE = 30L

        val DEFAULT: DetailMeetingUiModel =
            DetailMeetingUiModel(
                id = -1L,
                name = "-",
                dateTime = "0",
                destinationAddress = "-",
                departureAddress = "-",
                departureTime = "-",
                routeTime = "-",
                mates = listOf("-"),
                inviteCode = "",
            )
    }
}
