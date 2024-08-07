package com.woowacourse.ody.presentation.common

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

object CommonBindingAdapter {
    @BindingAdapter("visibility")
    @JvmStatic
    fun View.setVisibility(isVisible: Boolean?) {
        visibility = if (isVisible == true) View.GONE else View.VISIBLE
    }

    @BindingAdapter("enabledSwipe")
    @JvmStatic
    fun ViewPager2.setEnabledSwipe(isEnabled: Boolean?) {
        setUserInputEnabled(isEnabled ?: false)
    }
}
