package com.mulberry.ody.presentation.feature.room.etadashboard.adapter

import androidx.recyclerview.widget.RecyclerView
import com.mulberry.ody.databinding.ItemEtaDashboardBinding
import com.mulberry.ody.presentation.feature.room.etadashboard.listener.MissingToolTipListener
import com.mulberry.ody.presentation.feature.room.etadashboard.listener.NudgeListener
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel

class MateEtaViewHolder(private val binding: ItemEtaDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        mateEtaUiModel: MateEtaUiModel,
        missingToolTipListener: MissingToolTipListener,
        nudgeListener: NudgeListener,
    ) {
        binding.eta = mateEtaUiModel
        binding.missingTooltipListener = missingToolTipListener
        binding.nudgeListener = nudgeListener
    }
}
