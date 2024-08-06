package com.woowacourse.ody.presentation.meetinglist

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@BindingAdapter("setVisible")
fun View.setVisible(isVisible: Boolean) {
    visibility =
        if (isVisible) {
            TextView.VISIBLE
        } else {
            TextView.GONE
        }
}

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
                this.context.getString(R.string.intro_today)
            }

            isTomorrow -> {
                this.context.getString(R.string.intro_tomorrow)
            }

            (
                meetingDay >= today.plusDays(2) &&
                    meetingDay <= today.plusDays(7)
            ) -> {
                this.context.getString(
                    R.string.intro_post_tomorrow,
                    meetingDay.dayOfYear - today.dayOfYear,
                )
            }

            else -> {
                DateTimeFormatter.ofPattern("yyyy년 M월 d일").withLocale(Locale.forLanguageTag("ko")).format(dateTime)
            }
        }
    val meetingTime = dateTime.toLocalTime()
    val timeString =
        if (isToday || isTomorrow) {
            DateTimeFormatter.ofPattern("a h시 mm분").withLocale(Locale.forLanguageTag("ko")).format(meetingTime)
        } else {
            ""
        }
    this.text = "$dateString $timeString"
}
