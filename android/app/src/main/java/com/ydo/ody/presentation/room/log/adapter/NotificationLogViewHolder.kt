package com.ydo.ody.presentation.room.log.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ydo.ody.databinding.ItemNotificationLogBinding
import com.ydo.ody.presentation.room.log.model.NotificationLogUiModel

class NotificationLogViewHolder(private val binding: ItemNotificationLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: NotificationLogUiModel) {
        binding.log = item
    }
}
