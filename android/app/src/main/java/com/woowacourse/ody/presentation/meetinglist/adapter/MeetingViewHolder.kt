package com.woowacourse.ody.presentation.meetinglist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingBinding
import com.woowacourse.ody.presentation.meetinglist.MeetingListViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingUiModel

class MeetingViewHolder(private val binding: ItemMeetingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingUiModel,
        viewModel: MeetingListViewModel,
    ) {
        binding.meeting = item
        binding.vm = viewModel
    }
}
