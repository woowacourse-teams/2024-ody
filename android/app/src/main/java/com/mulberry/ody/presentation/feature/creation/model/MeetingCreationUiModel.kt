package com.mulberry.ody.presentation.feature.creation.model

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.model.MeetingName
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.Long

data class MeetingCreationUiModel(
    val name: String = "",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val destination: Address? = null,
) {
    fun isValid(): Boolean {
        return isValidName() && isValidDate() && isValidTime() && isValidDestination()
    }

    private fun isValidName(): Boolean = runCatching { MeetingName(name) }.isSuccess

    private fun isValidDate(): Boolean =
        runCatching { MeetingDateTime(date, LocalTime.of(23, 59)) }.isSuccess

    private fun isValidTime(): Boolean = runCatching { MeetingDateTime(date, time) }.isSuccess

    private fun isValidDestination(): Boolean = destination?.isValid() ?: false

    fun convertMeetingCreationInfo(): MeetingCreationInfo? {
        if (!isValid()) {
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
                    Log.e("TEST", "restore destination ${destination}")
                    MeetingCreationUiModel(
                        name = it[0] as String,
                        date = (it[1] as String).toLocalDate(),
                        time = (it[2] as String).toLocalTime(),
                        destination = destination
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
    Log.e("TEST", "rememberSaveable")
    return rememberSaveable(saver = MeetingCreationUiModel.saver()) {
        MeetingCreationUiModel(
            name = name,
            date = date,
            time = time,
            destination = destination,
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
