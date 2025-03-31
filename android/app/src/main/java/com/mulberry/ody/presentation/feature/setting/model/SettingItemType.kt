package com.mulberry.ody.presentation.feature.setting.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mulberry.ody.R

interface SettingItemType {
    @get:DrawableRes
    val icon: Int

    @get:StringRes
    val description: Int
}

enum class SettingNotificationItemType(
    @DrawableRes override val icon: Int,
    @StringRes override val description: Int,
) : SettingItemType {
    NOTIFICATION_DEPARTURE(R.drawable.ic_notification, R.string.setting_item_notification_departure),
    NOTIFICATION_ENTRY(R.drawable.ic_notification, R.string.setting_item_notification_entry),
}

enum class SettingServiceItemType(
    @DrawableRes override val icon: Int,
    @StringRes override val description: Int,
) : SettingItemType {
    PRIVACY_POLICY(R.drawable.ic_user_info, R.string.setting_item_privacy_policy),
    TERM(R.drawable.ic_comment_info, R.string.setting_item_term),
    LOGOUT(R.drawable.ic_logout, R.string.setting_item_logout),
    WITHDRAW(R.drawable.ic_user_close, R.string.setting_item_withdraw),
}
