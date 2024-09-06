package com.ydo.ody.presentation.meetings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ydo.ody.databinding.ItemMeetingCatalogBinding
import com.ydo.ody.presentation.meetings.listener.MeetingsItemListener
import com.ydo.ody.presentation.meetings.listener.MeetingsListener
import com.ydo.ody.presentation.meetings.model.MeetingUiModel

class MeetingsViewHolder(private val binding: ItemMeetingCatalogBinding) :
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
