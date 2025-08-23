package com.mulberry.ody.presentation.feature.room.log

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.NotificationLogType

@BindingAdapter("notificationType")
fun TextView.setTextNotificationType(notificationLogType: NotificationLogType) {
    val stringRes =
        when (notificationLogType) {
            NotificationLogType.ENTRY -> R.string.item_notification_entry
            NotificationLogType.DEPARTURE_REMINDER -> R.string.item_notification_departure_reminder
            NotificationLogType.DEPARTURE -> R.string.item_notification_departure
            NotificationLogType.NUDGE -> R.string.item_notification_nudge
            NotificationLogType.MEMBER_DELETION -> R.string.item_notification_member_deletion
            NotificationLogType.MEMBER_EXIT -> R.string.item_notification_member_exit
            NotificationLogType.DEFAULT -> R.string.item_notification_default
        }
    text = context.getString(stringRes)
}
