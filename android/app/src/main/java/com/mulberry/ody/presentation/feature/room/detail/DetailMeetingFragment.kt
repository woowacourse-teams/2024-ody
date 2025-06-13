package com.mulberry.ody.presentation.feature.room.detail

import android.app.Activity
import android.content.Context.LAYOUT_INFLATER_SERVICE
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentDetailMeetingBinding
import com.mulberry.ody.databinding.LayoutDepartureTimeTooltipBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.common.toPixel
import com.mulberry.ody.presentation.feature.room.MeetingRoomActivity
import com.mulberry.ody.presentation.feature.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.feature.room.detail.adapter.MatesAdapter
import com.mulberry.ody.presentation.feature.room.detail.listener.DepartureTimeGuideListener
import com.mulberry.ody.presentation.feature.room.detail.listener.InviteCodeCopyListener
import com.mulberry.ody.presentation.feature.room.detail.model.InviteCodeCopyInfo
import com.mulberry.ody.presentation.feature.room.detail.model.InviteCodeCopyUiModel
import com.mulberry.ody.presentation.feature.room.detail.model.MatesUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.EtaDashboardScreen
import com.mulberry.ody.presentation.theme.OdyTheme

class DetailMeetingFragment :
    BindingFragment<FragmentDetailMeetingBinding>(R.layout.fragment_detail_meeting),
    DepartureTimeGuideListener,
    InviteCodeCopyListener {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val parentActivity: Activity by lazy { requireActivity() }
    private val matesAdapter: MatesAdapter by lazy { MatesAdapter(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
            )
            setContent {
                OdyTheme {
                    DetailMeetingScreen(
                        onBack = (requireActivity() as MeetingRoomActivity)::onBack,
                        onCopyInviteCode = ::onCopyInviteCode,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

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
        binding.rvDetailMeetingMates.adapter = matesAdapter
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.mates) {
            val mates: List<MatesUiModel> = it + listOf(InviteCodeCopyUiModel())
            matesAdapter.submitList(mates)
        }
        collectWhenStarted(viewModel.copyInviteCodeEvent) {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"

            val shareMessage =
                getString(R.string.invite_code_copy, it.meetingName, it.inviteCode)
            intent.putExtra(Intent.EXTRA_TEXT, shareMessage)

            val chooserTitle = getString(R.string.invite_code_copy_title)
            startActivity(Intent.createChooser(intent, chooserTitle))
        }
    }

    private fun onCopyInviteCode(info: InviteCodeCopyInfo) {
        val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
        intent.type = "text/plain"

        val shareMessage = getString(R.string.invite_code_copy, info.meetingName, info.inviteCode)
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage)

        val chooserTitle = getString(R.string.invite_code_copy_title)
        startActivity(Intent.createChooser(intent, chooserTitle))
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

    override fun onCopy() {
        viewModel.copyInviteCode()
    }

    companion object {
        private const val GUIDE_BUTTON_PADDING = 5
        private const val GUIDE_BUTTON_WIDTH = 12
    }
}
