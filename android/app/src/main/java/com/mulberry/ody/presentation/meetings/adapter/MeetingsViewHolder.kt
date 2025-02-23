package com.mulberry.ody.presentation.meetings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemMeetingBinding
import com.mulberry.ody.presentation.meetings.listener.MeetingsItemListener
import com.mulberry.ody.presentation.meetings.listener.MeetingsListener
import com.mulberry.ody.presentation.meetings.model.MeetingUiModel

class MeetingsViewHolder(private val binding: ItemMeetingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingUiModel,
        meetingsItemListener: MeetingsItemListener,
        meetingsListener: MeetingsListener,
    ) {
        binding.meeting = item
        binding.meetingsItemListener = meetingsItemListener
        binding.meetingsListener = meetingsListener
    }
}
