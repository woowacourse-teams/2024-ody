package com.woowacourse.ody.presentation.room.log

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.woowacourse.ody.R
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.NotificationType
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel

@BindingAdapter("notificationType")
fun TextView.setTextNotificationType(notificationType: NotificationType) {
    val stringRes =
        when (notificationType) {
            NotificationType.ENTRY -> R.string.item_notification_entry
            NotificationType.DEPARTURE_REMINDER -> R.string.item_notification_departure_reminder
            NotificationType.DEPARTURE -> R.string.item_notification_departure
            NotificationType.DEFAULT -> R.string.item_notification_default
        }
    text = context.getString(stringRes)
}

@BindingAdapter("mateCount")
fun TextView.setMateCountText(meeting: Meeting) {
    val mateCount = meeting.mates.size
    text = context.getString(R.string.notification_log_mate_count, mateCount)
}
