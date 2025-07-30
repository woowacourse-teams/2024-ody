package com.mulberry.ody.presentation.feature.creation.model

import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingName
import java.time.LocalDate
import java.time.LocalTime

data class MeetingCreationUiModel(
    val name: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val destination: Address? = null,
    val isValid: Boolean = false
) {
    fun isValidName(): Boolean = runCatching { MeetingName(name) }.isSuccess

    fun isValidDate(): Boolean = runCatching { MeetingDateTime(date, LocalTime.of(23, 59)) }.isSuccess

    fun isValidTime(): Boolean = runCatching { MeetingDateTime(date, time) }.isSuccess

    fun isValidDestination(): Boolean = destination?.isValid() ?: false

    fun convertMeetingCreationInfo(): MeetingCreationInfo? {
        if (!isValid) {
            return null
        }
        return MeetingCreationInfo(
            name = MeetingName(name),
            dateTime = MeetingDateTime(date, time),
            destinationAddress = destination ?: return null,
        )
    }
}
