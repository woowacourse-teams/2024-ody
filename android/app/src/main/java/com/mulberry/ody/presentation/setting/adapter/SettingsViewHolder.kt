package com.mulberry.ody.presentation.setting.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemSettingBinding
import com.mulberry.ody.presentation.setting.listener.SettingListener
import com.mulberry.ody.presentation.setting.model.SettingUiModel

class SettingsViewHolder(private val binding: ItemSettingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: SettingUiModel,
        settingListener: SettingListener,
    ) {
        binding.setting = item
        binding.settingListener = settingListener
    }
}
