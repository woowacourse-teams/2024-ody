package com.mulberry.ody.presentation.meetings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.presentation.common.toMessage
import java.time.LocalDateTime

@BindingAdapter("showDateTime")
fun TextView.showDateTime(dateTime: LocalDateTime) {
    val meetingDay = dateTime.toLocalDate()
    val today = LocalDateTime.now().toLocalDate()
    val tomorrow = today.plusDays(1)
    val isToday = meetingDay == today
    val isTomorrow = meetingDay == tomorrow

    val dateString =
        when {
            isToday -> {
                context.getString(R.string.meetings_today)
            }

            isTomorrow -> {
                context.getString(R.string.meetings_tomorrow)
            }

            meetingDay >= today.plusDays(2) && meetingDay <= today.plusDays(7) -> {
                context.getString(
                    R.string.meetings_post_tomorrow,
                    (meetingDay.dayOfYear - today.dayOfYear).toString(),
                )
            }

            else -> dateTime.toLocalDate().toMessage()
        }
    if (!(isToday || isTomorrow)) {
        this.text = dateString
        return
    }
    val timeString = dateTime.toLocalTime().toMessage()
    this.text = "$dateString $timeString"
}
