package com.mulberry.ody.presentation.setting.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mulberry.ody.R

enum class SettingItemType(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
    val isSwitch: Boolean = false,
) {
    NOTIFICATION_DEPARTURE(R.drawable.ic_notification, R.string.setting_item_notification_departure, isSwitch = true),
    NOTIFICATION_ENTRY(R.drawable.ic_notification, R.string.setting_item_notification_entry, isSwitch = true),
    PRIVACY_POLICY(R.drawable.ic_user_info, R.string.setting_item_privacy_policy),
    TERM(R.drawable.ic_comment_info, R.string.setting_item_term),
    LOGOUT(R.drawable.ic_logout, R.string.setting_item_logout),
    WITHDRAW(R.drawable.ic_user_close, R.string.setting_item_withdraw),
}
