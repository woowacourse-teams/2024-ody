package com.woowacourse.ody.presentation.meetings.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemMeetingCatalogBinding
import com.woowacourse.ody.presentation.meetings.listener.MeetingsListener
import com.woowacourse.ody.presentation.meetings.listener.ToggleFoldListener
import com.woowacourse.ody.presentation.meetings.model.MeetingUiModel

class MeetingsAdapter(
    private val toggleFoldListener: ToggleFoldListener,
    private val meetingsListener: MeetingsListener,
) : ListAdapter<MeetingUiModel, MeetingsViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MeetingsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMeetingCatalogBinding.inflate(inflater, parent, false)
        return MeetingsViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MeetingsViewHolder,
        position: Int,
    ) {
        holder.bind(
            currentList[position],
            toggleFoldListener,
            meetingsListener,
        )
    }

    override fun getItemCount(): Int = currentList.size

    class DiffCallback : DiffUtil.ItemCallback<MeetingUiModel>() {
        override fun areItemsTheSame(
            oldItem: MeetingUiModel,
            newItem: MeetingUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MeetingUiModel,
            newItem: MeetingUiModel,
        ): Boolean = oldItem == newItem
    }
}
