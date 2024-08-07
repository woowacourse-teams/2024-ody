package com.woowacourse.ody.presentation.meetings.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.meetings.listener.MeetingsListener
import com.woowacourse.ody.presentation.meetings.listener.ToggleFoldListener
import com.woowacourse.ody.presentation.meetings.model.MeetingUiModel

class MeetingsViewHolder(private val binding: ItemMeetingCatalogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingUiModel,
        toggleFoldListener: ToggleFoldListener,
        meetingsListener: MeetingsListener,
    ) {
        binding.meeting = item
        binding.toggleListener = toggleFoldListener
        binding.homeListener = meetingsListener
    }
}
