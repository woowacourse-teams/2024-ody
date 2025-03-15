package com.mulberry.ody.presentation.feature.meetings.model

import android.content.Context
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.toMessage
import java.time.LocalDate
import java.time.LocalDateTime

data class MeetingUiModel(
    val id: Long,
    val name: String,
    val datetime: LocalDateTime,
    val originAddress: String,
    val targetAddress: String,
    val durationMinutes: String,
) {
    fun isAccessible(): Boolean {
        val now = LocalDateTime.now()
        return datetime.isBefore(now.plusMinutes(30))
    }

    fun dateTimeMessage(context: Context): String {
        val meetingDate = datetime.toLocalDate()
        val meetingTimeMessage = datetime.toLocalTime().toMessage()

        return when {
            meetingDate.isToday() -> {
                context.getString(R.string.meetings_today) + " " + meetingTimeMessage
            }

            meetingDate.isTomorrow() -> {
                context.getString(R.string.meetings_tomorrow) + " " + meetingTimeMessage
            }

            meetingDate.isShowDaysLater() -> {
                val daysDifference = meetingDate.daysDifferenceByNow()
                context.getString(R.string.meetings_post_tomorrow, daysDifference)
            }

            else -> meetingDate.toMessage()
        }
    }

    private fun LocalDate.isToday(): Boolean {
        return this == LocalDate.now()
    }

    private fun LocalDate.isTomorrow(): Boolean {
        return this == LocalDate.now().plusDays(1)
    }

    private fun LocalDate.isShowDaysLater(): Boolean {
        return daysDifferenceByNow() in 2..7
    }

    private fun LocalDate.daysDifferenceByNow(): Int = this.dayOfYear - LocalDate.now().dayOfYear
}
