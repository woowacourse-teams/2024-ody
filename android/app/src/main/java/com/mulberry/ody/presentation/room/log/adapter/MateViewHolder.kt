package com.mulberry.ody.presentation.room.log.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemMateBinding
import com.mulberry.ody.presentation.room.log.model.MateUiModel

class MateViewHolder(private val binding: ItemMateBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(mate: MateUiModel) {
        binding.mate = mate
    }
}
