package com.woowacourse.ody.presentation.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemSettingBinding
import com.woowacourse.ody.presentation.setting.listener.SettingListener
import com.woowacourse.ody.presentation.setting.model.Setting

class SettingsAdapter(
    private val settingListener: SettingListener,
) : ListAdapter<Setting, SettingsViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSettingBinding.inflate(inflater, parent, false)
        return SettingsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: SettingsViewHolder,
        position: Int,
    ) {
        holder.bind(
            currentList[position],
            settingListener,
        )
    }

    override fun getItemCount(): Int = currentList.size

    class DiffCallback : DiffUtil.ItemCallback<Setting>() {
        override fun areItemsTheSame(
            oldItem: Setting,
            newItem: Setting,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: Setting,
            newItem: Setting,
        ): Boolean = oldItem == newItem
    }
}
