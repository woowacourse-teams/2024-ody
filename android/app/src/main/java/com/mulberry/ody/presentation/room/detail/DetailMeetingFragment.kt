package com.mulberry.ody.presentation.room.detail

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentDetailMeetingBinding
import com.mulberry.ody.databinding.LayoutDepartureTimeTooltipBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.toPixel
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.room.detail.listener.DepartureTimeGuideListener

class DetailMeetingFragment :
    BindingFragment<FragmentDetailMeetingBinding>(R.layout.fragment_detail_meeting),
    DepartureTimeGuideListener {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val parentActivity: Activity by lazy { requireActivity() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    fun initializeBinding() {
        binding.vm = viewModel
        binding.backListener = parentActivity as MeetingRoomActivity
        binding.meetingRoomListener = parentActivity as MeetingRoomActivity
        binding.departureTimeGuideListener = this
    }

    private fun initializeObserve() {
    }

    override fun toggleDepartureTimeGuide(point: Point) {
        val inflater = parentActivity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentViewGroup = parentActivity.findViewById<ViewGroup>(android.R.id.content)
        val binding = LayoutDepartureTimeTooltipBinding.inflate(inflater, parentViewGroup, false)
        val popupView = binding.root

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val adjustedPoint = calculateDepartureTimeGuidePoint(popupView, point)
        showPopup(adjustedPoint, popupView, parentActivity.window.decorView)
    }

    private fun calculateDepartureTimeGuidePoint(
        popupView: View,
        guideButtonPoint: Point,
    ): Point {
        val popupHeight = popupView.measuredHeight

        val guideButtonWidthPixel = GUIDE_BUTTON_WIDTH.toPixel(binding.root.context)
        val guideButtonPaddingPixel = GUIDE_BUTTON_PADDING.toPixel(binding.root.context)
        val adjustedX = guideButtonPoint.x + guideButtonWidthPixel + guideButtonPaddingPixel
        val adjustedY = guideButtonPoint.y - popupHeight + guideButtonPaddingPixel

        return Point(adjustedX, adjustedY)
    }

    private fun showPopup(
        point: Point,
        popupView: View,
        decorView: View,
    ) {
        val popupWindow =
            PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true,
            )
        popupWindow.showAtLocation(
            decorView,
            Gravity.NO_GRAVITY,
            point.x,
            point.y,
        )
    }

    companion object {
        private const val GUIDE_BUTTON_PADDING = 5
        private const val GUIDE_BUTTON_WIDTH = 12
    }
}
