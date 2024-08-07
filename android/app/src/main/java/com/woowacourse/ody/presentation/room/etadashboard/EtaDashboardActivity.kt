package com.woowacourse.ody.presentation.room.etadashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityEtaDashboardBinding
import com.woowacourse.ody.databinding.LayoutMissingTooltipBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.room.etadashboard.adapter.MateEtasAdapter
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener
import com.woowacourse.ody.presentation.room.log.listener.CopyInviteCodeListener
import com.woowacourse.ody.presentation.room.log.listener.ShareListener

class EtaDashboardActivity :
    BindingActivity<ActivityEtaDashboardBinding>(R.layout.activity_eta_dashboard),
    MissingToolTipListener,
    CopyInviteCodeListener,
    ShareListener,
    BackListener {
    private val viewModel: EtaDashboardViewModel by viewModels<EtaDashboardViewModel> {
        EtaDashboardViewModelFactory(
            meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE),
            matesEtaRepository = application.matesEtaRepository,
        )
    }
    private val adapter: MateEtasAdapter by lazy { MateEtasAdapter(this) }
    private val bottomSheetLayout by lazy { findViewById<ConstraintLayout>(R.id.cl_bottom_sheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeListAdapter()
        initializeObserve()
        initializePersistentBottomSheet()
    }

    private fun initializeListAdapter() {
        binding.rvDashboard.adapter = adapter
    }

    override fun initializeBinding() {
        binding.backListener = this
        binding.shareListener = this
        binding.copyInviteCodeListener = this
        binding.title = intent.getStringExtra(MEETING_TITLE_KEY)
    }

    private fun initializeObserve() {
        viewModel.mateEtaUiModels.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
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
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val parentViewGroup = findViewById<ViewGroup>(android.R.id.content)
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

        popupWindow.showAtLocation(window.decorView, Gravity.NO_GRAVITY, adjustedX, adjustedY)
    }

    override fun onShare() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCopyInviteCode() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val inviteCode = intent.getStringExtra(INVITE_CODE_KEY) ?: return
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(INVITE_CODE_LABEL, inviteCode)
        clipboard.setPrimaryClip(clip)
    }

    override fun onBack() {
        finish()
    }

    companion object {
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        private const val INVITE_CODE_KEY = "invite_code"
        private const val INVITE_CODE_LABEL = "inviteCode"

        private const val MEETING_TITLE_KEY = "meeting_title"

        fun getIntent(
            context: Context,
            meetingId: Long,
            inviteCode: String,
            title: String,
        ): Intent {
            return Intent(context, EtaDashboardActivity::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
                putExtra(INVITE_CODE_KEY, inviteCode)
                putExtra(MEETING_TITLE_KEY, title)
            }
        }
    }
}
