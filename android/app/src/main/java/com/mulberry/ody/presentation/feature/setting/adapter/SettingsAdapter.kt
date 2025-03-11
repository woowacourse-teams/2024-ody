package com.mulberry.ody.presentation.feature.setting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemSettingBinding
import com.mulberry.ody.databinding.ItemSettingDividerBinding
import com.mulberry.ody.databinding.ItemSettingHeaderBinding
import com.mulberry.ody.presentation.feature.setting.listener.SettingListener
import com.mulberry.ody.presentation.feature.setting.model.SettingHeader
import com.mulberry.ody.presentation.feature.setting.model.SettingItem
import com.mulberry.ody.presentation.feature.setting.model.SettingUiModel
import java.lang.IllegalStateException

class SettingsAdapter(
    private val settings: List<SettingUiModel>,
    private val settingListener: SettingListener,
) : RecyclerView.Adapter<SettingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SettingUiModel.SETTING_HEADER_VIEW_TYPE -> {
                val binding = ItemSettingHeaderBinding.inflate(inflater, parent, false)
                SettingHeaderViewHolder(binding)
            }

            SettingUiModel.SETTING_ITEM_VIEW_TYPE -> {
                val binding = ItemSettingBinding.inflate(inflater, parent, false)
                SettingItemViewHolder(binding)
            }

            SettingUiModel.SETTING_DIVIDER_VIEW_TYPE -> {
                val binding = ItemSettingDividerBinding.inflate(inflater, parent, false)
                SettingDividerViewHolder(binding)
            }

            else -> throw IllegalStateException("뷰 타입에 해당하는 뷰홀더가 존재하지 않습니다.")
        }
    }

    override fun onBindViewHolder(
        holder: SettingViewHolder,
        position: Int,
    ) {
        when (holder) {
            is SettingDividerViewHolder -> holder.bind()
            is SettingHeaderViewHolder -> holder.bind(settings[position] as SettingHeader)
            is SettingItemViewHolder -> holder.bind(settings[position] as SettingItem, settingListener)
        }
    }

    override fun getItemCount(): Int = settings.size

    override fun getItemViewType(position: Int): Int = settings[position].viewType
}
