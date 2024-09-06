package com.woowacourse.ody.presentation.setting.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.woowacourse.ody.R

enum class Setting(
    @DrawableRes val icon: Int,
    @StringRes val description: Int,
) {
    PRIVACY_POLICY(R.drawable.ic_user_info, R.string.item_privacy_policy),
    TERM(R.drawable.ic_comment_info, R.string.item_term),
    LOGOUT(R.drawable.ic_logout, R.string.item_logout),
    WITHDRAW(R.drawable.ic_user_close, R.string.item_withdraw),
}
