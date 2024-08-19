package com.woowacourse.ody.presentation.room.etadashboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.woowacourse.ody.databinding.ItemEtaDashboardBinding
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.woowacourse.ody.presentation.room.etadashboard.listener.NudgeListener
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel

class MateEtasAdapter(
    private val missingToolTipListener: MissingToolTipListener,
    private val nudgeListener: NudgeListener,
) : ListAdapter<MateEtaUiModel, MateEtaViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): MateEtaViewHolder {
        val binding = ItemEtaDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MateEtaViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MateEtaViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position), missingToolTipListener, nudgeListener)
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<MateEtaUiModel>() {
                override fun areItemsTheSame(
                    oldItem: MateEtaUiModel,
                    newItem: MateEtaUiModel,
                ): Boolean = oldItem == newItem

                override fun areContentsTheSame(
                    oldItem: MateEtaUiModel,
                    newItem: MateEtaUiModel,
                ): Boolean = oldItem == newItem
            }
    }
}
