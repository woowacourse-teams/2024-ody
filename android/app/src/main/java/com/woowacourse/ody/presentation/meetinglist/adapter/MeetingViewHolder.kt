package com.woowacourse.ody.presentation.meetinglist.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingBinding
import com.woowacourse.ody.presentation.meetinglist.MeetingsViewModel
import com.woowacourse.ody.presentation.meetinglist.model.MeetingCatalogUiModel

class MeetingViewHolder(private val binding: ItemMeetingBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingCatalogUiModel,
        viewModel: MeetingsViewModel,
    ) {
        binding.meeting = item
        binding.vm = viewModel
    }
}
