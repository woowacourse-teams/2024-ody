package com.mulberry.ody.presentation.common

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mulberry.ody.presentation.common.listener.SingleClickListener

@BindingAdapter("visibility")
fun View.setVisibility(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("enabledSwipe")
fun ViewPager2.setEnabledSwipe(isEnabled: Boolean?) {
    setUserInputEnabled(isEnabled ?: false)
}

@BindingAdapter("onSingleClick")
fun View.setOnSingleClickListener(onSingleClick: View.OnClickListener) {
    val singleClickListener = SingleClickListener { onSingleClick.onClick(this) }
    setOnClickListener(singleClickListener)
}
