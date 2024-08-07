package com.woowacourse.ody.presentation.meetings

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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

            else -> {
                DateTimeFormatter.ofPattern("yyyy년 M월 d일").format(dateTime)
            }
        }
    val meetingTime = dateTime.toLocalTime()
    if (!(isToday || isTomorrow)) {
        this.text = dateString
        return
    }
    val timeString =
        DateTimeFormatter.ofPattern("a h시 mm분").withLocale(Locale.forLanguageTag("ko"))
            .format(meetingTime)
    this.text = "$dateString $timeString"
}
