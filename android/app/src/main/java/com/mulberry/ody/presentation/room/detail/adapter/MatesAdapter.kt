package com.mulberry.ody.presentation.room.detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mulberry.ody.databinding.ItemInviteCodeCopyBinding
import com.mulberry.ody.databinding.ItemMateBinding
import com.mulberry.ody.presentation.room.detail.listener.InviteCodeCopyListener
import com.mulberry.ody.presentation.room.detail.model.MateUiModel
import com.mulberry.ody.presentation.room.detail.model.MatesUiModel
import java.lang.IllegalStateException

class MatesAdapter(
    private val inviteCodeCopyListener: InviteCodeCopyListener,
) :
    ListAdapter<MatesUiModel, MatesViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MatesViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            MatesUiModel.MATE_VIEW_TYPE -> {
                val binding = ItemMateBinding.inflate(inflater, parent, false)
                MateViewHolder(binding)
            }

            MatesUiModel.INVITE_CODE_COPY_VIEW_TYPE -> {
                val binding = ItemInviteCodeCopyBinding.inflate(inflater, parent, false)
                InviteCodeCopyViewHolder(binding)
            }

            else -> throw IllegalStateException("뷰 타입에 해당하는 뷰홀더가 존재하지 않습니다.")
        }
    }

    override fun onBindViewHolder(
        holder: MatesViewHolder,
        position: Int,
    ) {
        when (holder) {
            is MateViewHolder -> holder.bind(getItem(position) as MateUiModel)
            is InviteCodeCopyViewHolder -> holder.bind(inviteCodeCopyListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).viewType
    }

    class DiffCallback : DiffUtil.ItemCallback<MatesUiModel>() {
        override fun areItemsTheSame(
            oldItem: MatesUiModel,
            newItem: MatesUiModel,
        ): Boolean = oldItem == newItem

        override fun areContentsTheSame(
            oldItem: MatesUiModel,
            newItem: MatesUiModel,
        ): Boolean = oldItem == newItem
    }
}
