package com.woowacourse.ody.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.home.listener.HomeListener
import com.woowacourse.ody.presentation.home.listener.ToggleFoldListener
import com.woowacourse.ody.presentation.home.model.MeetingCatalogUiModel

class MeetingCatalogViewHolder(private val binding: ItemMeetingCatalogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingCatalogUiModel,
        toggleFoldListener: ToggleFoldListener,
        homeListener: HomeListener,
    ) {
        binding.meeting = item
        binding.toggleListener = toggleFoldListener
        binding.homeListener = homeListener
    }
}
