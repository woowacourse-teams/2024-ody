package com.woowacourse.ody.presentation.notificationlog

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import com.woowacourse.ody.domain.NotificationType
import com.woowacourse.ody.presentation.notificationlog.uimodel.NotificationLogUiModel

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

@BindingAdapter("setMatesText")
fun TextView.setMatesText(mates: List<String>?) {
    val num = mates?.size
    val names = mates?.joinToString(", ")
    this.text = this.context.getString(R.string.activity_notification_mates, num, names)
}
