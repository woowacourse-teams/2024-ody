package com.woowacourse.ody.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.home.HomeViewModel
import com.woowacourse.ody.presentation.home.model.MeetingCatalogUiModel

class MeetingCatalogsAdapter(
    private val viewModel: HomeViewModel,
) : ListAdapter<MeetingCatalogUiModel, MeetingCatalogViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MeetingCatalogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMeetingCatalogBinding.inflate(inflater, parent, false)
        return MeetingCatalogViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MeetingCatalogViewHolder,
        position: Int,
    ) {
        holder.bind(
            currentList[position],
            viewModel,
        )
    }

    override fun getItemCount(): Int = currentList.size

    class DiffCallback : DiffUtil.ItemCallback<MeetingCatalogUiModel>() {
        override fun areItemsTheSame(
            oldItem: MeetingCatalogUiModel,
            newItem: MeetingCatalogUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MeetingCatalogUiModel,
            newItem: MeetingCatalogUiModel,
        ): Boolean = oldItem == newItem
    }
}
