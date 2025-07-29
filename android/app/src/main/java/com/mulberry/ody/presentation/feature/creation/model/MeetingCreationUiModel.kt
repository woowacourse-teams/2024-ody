package com.mulberry.ody.presentation.feature.creation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingName
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.Long

@Stable
class MeetingCreationUiModel(
    initialName: String = "",
    initialDate: LocalDate = LocalDate.now(),
    initialTime: LocalTime = LocalTime.now(),
    initialDestination: Address? = null,
    initialIsValid: Boolean = false,
) {
    var name: String by mutableStateOf(initialName)
        private set

    var date: LocalDate by mutableStateOf(initialDate)
        private set

    var time: LocalTime by mutableStateOf(initialTime)
        private set

    var destination: Address? by mutableStateOf(initialDestination)
        private set

    var isValid: Boolean by mutableStateOf(initialIsValid)
        private set

    fun updateName(name: String) {
        this.name = name
        isValid = isValidName()
    }

    fun updateDate(date: LocalDate) {
        this.date = date
        isValid = isValidDate()
    }

    fun updateTime(time: LocalTime) {
        this.time = time
        isValid = isValidTime()
    }

    fun updateDestination(destination: Address) {
        this.destination = destination
        isValid = isValidDestination()
    }

    private fun isValidName(): Boolean = runCatching { MeetingName(name) }.isSuccess

    private fun isValidDate(): Boolean = runCatching { MeetingDateTime(date, LocalTime.of(23, 59)) }.isSuccess

    private fun isValidTime(): Boolean = runCatching { MeetingDateTime(date, time) }.isSuccess

    private fun isValidDestination(): Boolean = destination?.isValid() ?: false

    fun convertMeetingCreationInfo(): MeetingCreationInfo? {
        if (!isValid) {
            return null
        }
        return MeetingCreationInfo(
            name = MeetingName(name),
            dateTime = MeetingDateTime(date, time),
            destinationAddress = destination ?: return null
        )
    }

    companion object {
        fun saver(): Saver<MeetingCreationUiModel, *> =
            listSaver(
                save = {
                    listOf(
                        it.name,
                        it.date.toString(),
                        it.time.toString(),
                        it.destination?.id,
                        it.destination?.placeName,
                        it.destination?.detailAddress,
                        it.destination?.longitude,
                        it.destination?.latitude,
                        it.isValid,
                    )
                },
                restore = {
                    val destination =
                        if (it[3] == null) {
                            null
                        } else {
                            Address(
                                id = it[3] as Long,
                                placeName = it[4] as String,
                                detailAddress = it[5] as String,
                                longitude = it[6] as String,
                                latitude = it[7] as String,
                            )
                        }
                    MeetingCreationUiModel(
                        initialName = it[0] as String,
                        initialDate = (it[1] as String).toLocalDate(),
                        initialTime = (it[2] as String).toLocalTime(),
                        initialDestination = destination,
                        initialIsValid = it[8] as Boolean,
                    )
                }
            )
    }
}

@Composable
fun rememberSaveableMeetingCreationUiModel(
    name: String = "",
    date: LocalDate = LocalDate.now(),
    time: LocalTime = LocalTime.now(),
    destination: Address? = null,
): MeetingCreationUiModel {
    return rememberSaveable(saver = MeetingCreationUiModel.saver()) {
        MeetingCreationUiModel(
            initialName = name,
            initialDate = date,
            initialTime = time,
            initialDestination = destination,
        )
    }
}

private fun String.toLocalDate(): LocalDate {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, formatter)
}

private fun String.toLocalTime(): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(this, formatter)
}
