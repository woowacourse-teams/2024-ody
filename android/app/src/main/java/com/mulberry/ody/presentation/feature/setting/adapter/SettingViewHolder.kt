package com.mulberry.ody.presentation.feature.setting.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemSettingBinding
import com.mulberry.ody.databinding.ItemSettingDividerBinding
import com.mulberry.ody.databinding.ItemSettingHeaderBinding
import com.mulberry.ody.presentation.feature.setting.listener.SettingListener
import com.mulberry.ody.presentation.feature.setting.model.SettingHeader
import com.mulberry.ody.presentation.feature.setting.model.SettingItem

sealed class SettingViewHolder(view: View) : RecyclerView.ViewHolder(view)

class SettingHeaderViewHolder(private val binding: ItemSettingHeaderBinding) :
    SettingViewHolder(binding.root) {
    fun bind(item: SettingHeader) {
        binding.header = item
    }
}

class SettingItemViewHolder(private val binding: ItemSettingBinding) :
    SettingViewHolder(binding.root) {
    fun bind(
        item: SettingItem,
        settingListener: SettingListener,
    ) {
        settingListener.onInitializeSettingSwitchItem(binding.switchSetting, item.type)
        binding.setting = item
        binding.settingListener = settingListener
    }
}

class SettingDividerViewHolder(private val binding: ItemSettingDividerBinding) :
    SettingViewHolder(binding.root) {
    fun bind() = Unit
}
