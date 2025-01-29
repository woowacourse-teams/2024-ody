package com.mulberry.ody.presentation.meetings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.toMessage
import java.time.LocalDate
import java.time.LocalDateTime

@BindingAdapter("meetingDateTime")
fun TextView.setMeetingDateTime(meetingDateTime: LocalDateTime) {
    val meetingDate = meetingDateTime.toLocalDate()
    val meetingTimeMessage = meetingDateTime.toLocalTime().toMessage()

    text = when {
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
