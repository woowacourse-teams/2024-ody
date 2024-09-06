package com.ydo.ody.presentation.room.etadashboard.adapter

import androidx.recyclerview.widget.RecyclerView
import com.ydo.ody.databinding.ItemEtaDashboardBinding
import com.ydo.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.ydo.ody.presentation.room.etadashboard.listener.NudgeListener
import com.ydo.ody.presentation.room.etadashboard.model.MateEtaUiModel

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
