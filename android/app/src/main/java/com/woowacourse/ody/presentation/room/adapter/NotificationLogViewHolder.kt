package com.woowacourse.ody.presentation.room.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemNotificationLogBinding
import com.woowacourse.ody.presentation.room.model.NotificationLogUiModel

class NotificationLogViewHolder(private val binding: ItemNotificationLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: NotificationLogUiModel) {
        binding.log = item
    }
}
