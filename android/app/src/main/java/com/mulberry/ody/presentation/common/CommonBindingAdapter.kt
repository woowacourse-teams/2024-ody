package com.mulberry.ody.presentation.common

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide

@BindingAdapter("visibility")
fun View.setVisibility(isVisible: Boolean?) {
    visibility = if (isVisible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("enabledSwipe")
fun ViewPager2.setEnabledSwipe(isEnabled: Boolean?) {
    setUserInputEnabled(isEnabled ?: false)
}

@BindingAdapter("circleImageUrl")
fun ImageView.setCircleImageUrl(imageUrl: String) {
    if (imageUrl.isEmpty()) return
    Glide.with(context)
        .load(imageUrl)
        .circleCrop()
        .into(this)
}

@BindingAdapter("imageRes")
fun ImageView.setImageRes(
    @DrawableRes iconRes: Int,
) {
    setImageResource(iconRes)
}
