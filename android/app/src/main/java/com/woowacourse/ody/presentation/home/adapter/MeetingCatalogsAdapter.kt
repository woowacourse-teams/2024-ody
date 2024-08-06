package com.woowacourse.ody.presentation.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.home.listener.HomeListener
import com.woowacourse.ody.presentation.home.listener.ToggleFoldListener
import com.woowacourse.ody.presentation.home.model.MeetingCatalogUiModel

class MeetingCatalogsAdapter(
    private val toggleFoldListener: ToggleFoldListener,
    private val homeListener: HomeListener,
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
            toggleFoldListener,
            homeListener,
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
