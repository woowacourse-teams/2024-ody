package com.woowacourse.ody.presentation.room.log.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemMateBinding
import com.woowacourse.ody.presentation.room.log.model.MateUiModel

class MatesAdapter :
    ListAdapter<MateUiModel, MateViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MateViewHolder {
        val binding =
            ItemMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MateViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<MateUiModel>() {
        override fun areItemsTheSame(
            oldItem: MateUiModel,
            newItem: MateUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MateUiModel,
            newItem: MateUiModel,
        ): Boolean = oldItem == newItem
    }
}
