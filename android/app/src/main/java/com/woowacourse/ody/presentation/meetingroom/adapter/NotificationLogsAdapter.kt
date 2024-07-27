package com.woowacourse.ody.presentation.meetingroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemInviteCodeLogBinding
import com.woowacourse.ody.databinding.ItemNotificationLogBinding
import com.woowacourse.ody.presentation.meetingroom.CopyInviteCodeListener
import com.woowacourse.ody.presentation.meetingroom.uimodel.NotificationLogUiModel

class NotificationLogsAdapter(
    private val copyInviteCodeListener: CopyInviteCodeListener,
) : ListAdapter<NotificationLogUiModel, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_INVITE_CODE -> {
                val binding = ItemInviteCodeLogBinding.inflate(inflater, parent, false)
                InviteCodeViewHolder(binding)
            }

            TYPE_NOTIFICATION_LOG -> {
                val binding = ItemNotificationLogBinding.inflate(inflater, parent, false)
                NotificationLogViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
    ) {
        when (holder) {
            is InviteCodeViewHolder -> holder.bind(copyInviteCodeListener)
            is NotificationLogViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) TYPE_INVITE_CODE else TYPE_NOTIFICATION_LOG

    override fun getItemCount(): Int = currentList.size + ITEM_PRE

    override fun getItem(position: Int): NotificationLogUiModel {
        return super.getItem(position - ITEM_PRE)
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

    companion object {
        const val TYPE_INVITE_CODE = 0
        const val TYPE_NOTIFICATION_LOG = 1
        const val ITEM_PRE = 1
    }
}
