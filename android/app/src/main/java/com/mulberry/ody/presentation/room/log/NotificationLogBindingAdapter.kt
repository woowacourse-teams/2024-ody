package com.mulberry.ody.presentation.room.log

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.mulberry.ody.R
import com.mulberry.ody.domain.model.LogType

@BindingAdapter("notificationType")
fun TextView.setTextNotificationType(logType: LogType) {
    val stringRes =
        when (logType) {
            LogType.ENTRY -> R.string.item_notification_entry
            LogType.DEPARTURE_REMINDER -> R.string.item_notification_departure_reminder
            LogType.DEPARTURE -> R.string.item_notification_departure
            LogType.NUDGE -> R.string.item_notification_nudge
            LogType.MEMBER_DELETION -> R.string.item_notification_member_deletion
            LogType.MEMBER_EXIT -> R.string.item_notification_member_exit
            LogType.DEFAULT -> R.string.item_notification_default
        }
    text = context.getString(stringRes)
}
