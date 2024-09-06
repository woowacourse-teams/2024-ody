package com.ydo.ody.presentation.common

import android.view.View
import android.widget.ImageView
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

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(imageUrl: String) {
    Glide.with(context)
        .load(imageUrl)
        .into(this)
}

@BindingAdapter("circleImageUrl")
fun ImageView.setCircleImageUrl(imageUrl: String) {
    Glide.with(context)
        .load(imageUrl)
        .circleCrop()
        .into(this)
}
