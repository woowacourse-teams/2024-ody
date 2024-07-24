package com.woowacourse.ody.presentation.notificationlog.adapter

import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemInviteCodeLogBinding
import com.woowacourse.ody.presentation.notificationlog.CopyInviteCodeButtonListener

class InviteCodeViewHolder(private val binding: ItemInviteCodeLogBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(listener: CopyInviteCodeButtonListener) {
        binding.listener = listener
    }
}
