package com.woowacourse.ody.presentation.setting.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemSettingBinding
import com.woowacourse.ody.presentation.setting.listener.SettingListener
import com.woowacourse.ody.presentation.setting.model.Setting

class SettingsViewHolder(private val binding: ItemSettingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: Setting,
        settingListener: SettingListener,
    ) {
        binding.setting = item
        binding.settingListener = settingListener
    }
}
