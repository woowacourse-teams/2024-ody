package com.mulberry.ody.presentation.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mulberry.ody.databinding.ItemSettingBinding
import com.mulberry.ody.presentation.setting.listener.SettingListener
import com.mulberry.ody.presentation.setting.model.SettingUiModel

class SettingsAdapter(
    private val settingListener: SettingListener,
) : ListAdapter<SettingUiModel, SettingsViewHolder>(DiffCallback()) {
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

    class DiffCallback : DiffUtil.ItemCallback<SettingUiModel>() {
        override fun areItemsTheSame(
            oldItem: SettingUiModel,
            newItem: SettingUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: SettingUiModel,
            newItem: SettingUiModel,
        ): Boolean = oldItem == newItem
    }
}
