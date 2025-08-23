package com.mulberry.ody.domain.model

import android.content.Context
import androidx.annotation.StringRes
import com.mulberry.ody.R

enum class NotificationLogType(
    @StringRes val messageResId: Int,
) {
    ENTRY(R.string.item_notification_entry),
    DEPARTURE_REMINDER(R.string.item_notification_departure_reminder),
    DEPARTURE(R.string.item_notification_departure),
    NUDGE(R.string.item_notification_nudge),
    MEMBER_DELETION(R.string.item_notification_member_deletion),
    MEMBER_EXIT(R.string.item_notification_member_exit),
    DEFAULT(R.string.item_notification_default),
    ;

    fun getMessage(context: Context): String {
        return context.getString(messageResId)
    }
}
