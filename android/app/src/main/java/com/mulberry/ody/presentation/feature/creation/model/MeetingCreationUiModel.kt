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
) {
    fun isValid(type: MeetingCreationType): Boolean {
        return when (type) {
            MeetingCreationType.NAME -> isValidName()
            MeetingCreationType.DATE -> isValidDate()
            MeetingCreationType.TIME -> isValidTime()
            MeetingCreationType.DESTINATION -> isValidDestination()
        }
    }

    fun isValidName(): Boolean = runCatching { MeetingName(name) }.isSuccess

    fun isValidDate(): Boolean = MeetingDateTime(date, LocalTime.of(23, 59)).isValid()

    fun isValidTime(): Boolean = MeetingDateTime(date, time).isValid()

    fun isValidDestination(): Boolean = destination?.isValid() ?: false

    fun convertMeetingCreationInfo(): MeetingCreationInfo? {
        return MeetingCreationInfo(
            name = MeetingName(name),
            dateTime = MeetingDateTime(date, time),
            destinationAddress = destination ?: return null,
        )
    }
}
