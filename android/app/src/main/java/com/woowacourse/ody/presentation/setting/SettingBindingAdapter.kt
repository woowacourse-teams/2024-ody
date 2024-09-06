package com.woowacourse.ody.presentation.setting

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter

@BindingAdapter("icon")
fun ImageView.icon(
    @DrawableRes iconRes: Int,
) {
    setImageResource(iconRes)
}
