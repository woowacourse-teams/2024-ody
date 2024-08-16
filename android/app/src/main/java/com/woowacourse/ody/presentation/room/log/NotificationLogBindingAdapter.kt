package com.woowacourse.ody.presentation.room.log

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationType
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel

@BindingAdapter("setNotificationLogText")
fun TextView.setNotificationLogText(log: NotificationLogUiModel) {
    this.text =
        when (log.type) {
            NotificationType.ENTRY ->
                this.context.getString(
                    R.string.item_notification_entry,
                    log.nickname,
                )

            NotificationType.DEPARTURE_REMINDER ->
                this.context.getString(
                    R.string.item_notification_departure_reminder,
                    log.nickname,
                )

            NotificationType.DEPARTURE ->
                this.context.getString(
                    R.string.item_notification_departure,
                    log.nickname,
                )

            NotificationType.DEFAULT -> ""
        }
}

@BindingAdapter("mateCount")
fun TextView.setMateCountText(meeting: Meeting) {
    val mateCount = meeting.mates.size
    text = context.getString(R.string.notification_log_mate_count, mateCount)
}
