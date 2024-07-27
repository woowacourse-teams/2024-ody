package com.woowacourse.ody.presentation.meetingroom.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemNotificationLogBinding
import com.woowacourse.ody.presentation.meetingroom.uimodel.NotificationLogUiModel

class NotificationLogViewHolder(private val binding: ItemNotificationLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: NotificationLogUiModel) {
        binding.log = item
    }
}
