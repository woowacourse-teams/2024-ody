package com.mulberry.ody.presentation.room.etadashboard

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
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.databinding.FragmentEtaDashboardBinding
import com.mulberry.ody.databinding.LayoutMissingTooltipBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.image.getBitmap
import com.mulberry.ody.presentation.common.image.toByteArray
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.room.etadashboard.adapter.MateEtasAdapter
import com.mulberry.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.mulberry.ody.presentation.room.etadashboard.listener.ShareListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EtaDashboardFragment :
    BindingFragment<FragmentEtaDashboardBinding>(R.layout.fragment_eta_dashboard),
    MissingToolTipListener,
    ShareListener {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val adapter: MateEtasAdapter by lazy { MateEtasAdapter(this, viewModel) }
    private val parentActivity: Activity by lazy { requireActivity() }

    @Inject lateinit var odyDatastore: OdyDatastore

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeGuide()
        initializeMateEtaList()
        initializeBinding()
        initializeObserve()
    }

    private fun initializeGuide() {
        lifecycleScope.launch {
            val isFirstSeenEtaDashboard = odyDatastore.getIsFirstSeenEtaDashboard().first()
            if (isFirstSeenEtaDashboard) {
                startEtaDashboardGuide()
                odyDatastore.setIsFirstSeenEtaDashboard(false)
            }
        }
    }

    private fun startEtaDashboardGuide() {
        childFragmentManager.commit {
            add(R.id.fcv_eta_dashboard, EtaDashboardGuideFirstFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    private fun initializeMateEtaList() {
        binding.rvEtaDashboard.adapter = adapter
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.shareListener = this
        binding.backListener = requireActivity() as MeetingRoomActivity
    }

    private fun initializeObserve() {
        viewModel.mateEtaUiModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        viewModel.nudgeSuccessMate.observe(viewLifecycleOwner) { nickName ->
            showSnackBar(getString(R.string.nudge_success, nickName))
        }
    }

    override fun onClickMissingToolTipListener(
        point: Point,
        isUserSelf: Boolean,
    ) {
        val messageId =
            if (isUserSelf) R.string.location_permission_self_guide else R.string.location_permission_friend_guide
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

        popupWindow.showAtLocation(
            parentActivity.window.decorView,
            Gravity.NO_GRAVITY,
            adjustedX,
            adjustedY,
        )
    }

    override fun onShare() {
        val bitmap = binding.rvEtaDashboard.getBitmap()
        val byteArray = bitmap.toByteArray()
        viewModel.shareEtaDashboard(
            title = getString(R.string.eta_dashboard_share_description),
            buttonTitle = getString(R.string.eta_dashboard_share_button),
            imageByteArray = byteArray,
            imageWidthPixel = bitmap.width,
            imageHeightPixel = bitmap.height,
        )
    }
}
