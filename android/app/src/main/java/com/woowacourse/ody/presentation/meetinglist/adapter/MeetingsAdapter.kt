package com.woowacourse.ody.presentation.meetinglist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemMeetingBinding
import com.woowacourse.ody.presentation.meetinglist.MeetingsViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel

class MeetingsAdapter(
    private val viewModel: MeetingsViewModel,
) : ListAdapter<MeetingUiModel, MeetingViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MeetingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMeetingBinding.inflate(inflater, parent, false)
        return MeetingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MeetingViewHolder,
        position: Int,
    ) {
        holder.bind(
            currentList[position],
            viewModel,
        )
    }

    override fun getItemCount(): Int = currentList.size

    class DiffCallback : DiffUtil.ItemCallback<MeetingUiModel>() {
        override fun areItemsTheSame(
            oldItem: MeetingUiModel,
            newItem: MeetingUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MeetingUiModel,
            newItem: MeetingUiModel,
        ): Boolean = oldItem == newItem
    }
}
