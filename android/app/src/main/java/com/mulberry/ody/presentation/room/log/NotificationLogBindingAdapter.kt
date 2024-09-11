package com.mulberry.ody.presentation.room.log

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.NotificationType

@BindingAdapter("notificationType")
fun TextView.setTextNotificationType(notificationType: NotificationType) {
    val stringRes =
        when (notificationType) {
            NotificationType.ENTRY -> R.string.item_notification_entry
            NotificationType.DEPARTURE_REMINDER -> R.string.item_notification_departure_reminder
            NotificationType.DEPARTURE -> R.string.item_notification_departure
            NotificationType.NUDGE -> R.string.item_notification_nudge
            NotificationType.MEMBER_DELETION -> R.string.item_notification_member_deletion
            NotificationType.DEFAULT -> R.string.item_notification_default
        }
    text = context.getString(stringRes)
}
