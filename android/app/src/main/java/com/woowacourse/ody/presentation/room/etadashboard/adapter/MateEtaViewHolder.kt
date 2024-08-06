package com.woowacourse.ody.presentation.room.etadashboard.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.woowacourse.ody.databinding.ItemEtaDashboardBinding
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel

class MateEtaViewHolder(private val binding: ItemEtaDashboardBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(
        mateEtaUiModel: MateEtaUiModel,
        missingToolTipListener: MissingToolTipListener,
    ) {
        binding.eta = mateEtaUiModel
        binding.tvMissingTooltip.setOnClickListener {
            val (x, y) = binding.tvMissingTooltip.getPointOnScreen()
            missingToolTipListener.onClickMissingToolTipListener(x, y, mateEtaUiModel.isUserSelf)
        }
    }

    private fun View.getPointOnScreen(): Pair<Int, Int> {
        val location = IntArray(2)
        this.getLocationOnScreen(location)
        return location[0] to location[1]
    }
}
