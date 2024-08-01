package com.woowacourse.ody.presentation.room.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemInviteCodeLogBinding
import com.woowacourse.ody.presentation.room.listener.CopyInviteCodeListener

class InviteCodeViewHolder(private val binding: ItemInviteCodeLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(listener: CopyInviteCodeListener) {
        binding.listener = listener
    }
}
