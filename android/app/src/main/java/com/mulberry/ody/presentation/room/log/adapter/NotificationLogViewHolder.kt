package com.mulberry.ody.presentation.room.log.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemNotificationLogBinding
import com.mulberry.ody.presentation.room.log.model.NotificationLogUiModel

class NotificationLogViewHolder(private val binding: ItemNotificationLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: NotificationLogUiModel) {
        binding.log = item
    }
}
