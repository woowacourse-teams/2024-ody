package com.woowacourse.ody.presentation.room.log.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemEtaDashboardBinding
import com.woowacourse.ody.databinding.ItemInviteCodeLogBinding
import com.woowacourse.ody.databinding.ItemNotificationLogBinding
import com.woowacourse.ody.presentation.room.etadashboard.adapter.MateEtaViewHolder
import com.woowacourse.ody.presentation.room.log.model.NotificationLogUiModel

class NotificationLogsAdapter :
    ListAdapter<NotificationLogUiModel, NotificationLogViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NotificationLogViewHolder {
        val binding =
            ItemNotificationLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationLogViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationLogViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationLogUiModel>() {
        override fun areItemsTheSame(
            oldItem: NotificationLogUiModel,
            newItem: NotificationLogUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: NotificationLogUiModel,
            newItem: NotificationLogUiModel,
        ): Boolean = oldItem == newItem
    }
}
