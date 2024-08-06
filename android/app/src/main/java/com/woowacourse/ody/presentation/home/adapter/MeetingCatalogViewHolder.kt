package com.woowacourse.ody.presentation.home.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.home.HomeViewModel
import com.woowacourse.ody.presentation.home.model.MeetingCatalogUiModel

class MeetingCatalogViewHolder(private val binding: ItemMeetingCatalogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        item: MeetingCatalogUiModel,
        viewModel: HomeViewModel,
    ) {
        binding.meeting = item
        binding.vm = viewModel
    }
}
