package com.mulberry.ody.presentation.room.detail.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemInviteCodeCopyBinding
import com.mulberry.ody.databinding.ItemMateBinding
import com.mulberry.ody.presentation.room.detail.listener.InviteCodeCopyListener
import com.mulberry.ody.presentation.room.detail.model.MateUiModel

sealed class MatesViewHolder(view: View) : RecyclerView.ViewHolder(view)

class MateViewHolder(private val binding: ItemMateBinding) :
    MatesViewHolder(binding.root) {
    fun bind(mate: MateUiModel) {
        binding.mate = mate
    }
}

class InviteCodeCopyViewHolder(
    private val binding: ItemInviteCodeCopyBinding,
) :
    MatesViewHolder(binding.root) {
    fun bind(inviteCodeCopyListener: InviteCodeCopyListener) {
        binding.inviteCodeCopyListener = inviteCodeCopyListener
    }
}
