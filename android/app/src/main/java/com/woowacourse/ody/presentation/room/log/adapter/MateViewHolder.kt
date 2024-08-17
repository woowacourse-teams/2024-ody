package com.woowacourse.ody.presentation.room.log.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMateBinding
import com.woowacourse.ody.presentation.room.log.model.MateUiModel

class MateViewHolder(private val binding: ItemMateBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(mate: MateUiModel) {
        binding.mate = mate
    }
}
