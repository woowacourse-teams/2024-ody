package com.woowacourse.ody.presentation.meetings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.meetings.listener.MeetingsItemListener
import com.woowacourse.ody.presentation.meetings.listener.MeetingsListener
import com.woowacourse.ody.presentation.meetings.model.MeetingUiModel

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
