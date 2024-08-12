package com.woowacourse.ody.presentation.room.etadashboard

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.StringRes
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentEtaDashboardBinding
import com.woowacourse.ody.databinding.LayoutMissingTooltipBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.room.MeetingRoomViewModel
import com.woowacourse.ody.presentation.room.etadashboard.adapter.MateEtasAdapter
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener

class EtaDashboardFragment :
    BindingFragment<FragmentEtaDashboardBinding>(R.layout.fragment_eta_dashboard),
    MissingToolTipListener {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val adapter: MateEtasAdapter by lazy { MateEtasAdapter(this) }
    private val parentActivity: Activity by lazy { requireActivity() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeMateEtaList()
        initializeObserve()
    }

    private fun initializeMateEtaList() {
        binding.rvDashboard.adapter = adapter
    }

    private fun initializeObserve() {
        viewModel.mateEtaUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onClickMissingToolTipListener(
        point: Point,
        isUserSelf: Boolean,
    ) {
        val messageId = if (isUserSelf) R.string.location_permission_self_guide else R.string.location_permission_friend_guide
        showPopupWindow(messageId, point)
    }

    private fun showPopupWindow(
        @StringRes messageId: Int,
        point: Point,
    ) {
        val inflater = parentActivity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentViewGroup = parentActivity.findViewById<ViewGroup>(android.R.id.content)
        val binding = LayoutMissingTooltipBinding.inflate(inflater, parentViewGroup, false)
        val popupView = binding.root
        binding.tvTooltip.text = getString(messageId)

        val popupWindow =
            PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true,
            )

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView.measuredWidth
        val popupHeight = popupView.measuredHeight

        val adjustedX = point.x - popupWidth
        val adjustedY = point.y - popupHeight

        popupWindow.showAtLocation(parentActivity.window.decorView, Gravity.NO_GRAVITY, adjustedX, adjustedY)
    }
}
