package com.mulberry.ody.presentation.setting.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mulberry.ody.R

enum class SettingUiModel(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
) {
    PRIVACY_POLICY(R.drawable.ic_user_info, R.string.item_privacy_policy),
    TERM(R.drawable.ic_comment_info, R.string.item_term),
    LOGOUT(R.drawable.ic_logout, R.string.item_logout),
    WITHDRAW(R.drawable.ic_user_close, R.string.item_withdraw),
}
