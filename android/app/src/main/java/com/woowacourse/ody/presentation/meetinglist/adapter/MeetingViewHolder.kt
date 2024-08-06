package com.woowacourse.ody.presentation.meetinglist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingBinding
import com.woowacourse.ody.presentation.meetinglist.listener.MeetingItemListener
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel

class MeetingViewHolder(private val binding: ItemMeetingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingUiModel,
        listener: MeetingItemListener,
        folded: Boolean,
    ) {
        binding.meeting = item
        binding.listener = listener
        binding.folded = folded
    }
}
