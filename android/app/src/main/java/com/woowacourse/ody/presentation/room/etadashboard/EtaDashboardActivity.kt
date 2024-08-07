package com.woowacourse.ody.presentation.room.etadashboard

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
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityEtaDashboardBinding
import com.woowacourse.ody.databinding.LayoutMissingTooltipBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.room.etadashboard.adapter.MateEtasAdapter
import com.woowacourse.ody.presentation.room.etadashboard.listener.MissingToolTipListener

class EtaDashboardActivity :
    BindingActivity<ActivityEtaDashboardBinding>(R.layout.activity_eta_dashboard),
    MissingToolTipListener {
    private val viewModel: EtaDashboardViewModel by viewModels<EtaDashboardViewModel> {
        EtaDashboardViewModelFactory(
            meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE),
            matesEtaRepository = application.matesEtaRepository,
        )
    }
    private val adapter: MateEtasAdapter by lazy { MateEtasAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeMateEtaList()
        initializeObserve()
    }

    private fun initializeMateEtaList() {
        binding.rvDashboard.adapter = adapter
        binding.rvDashboard.itemAnimator = null
    }

    override fun initializeBinding() {
    }

    private fun initializeObserve() {
        viewModel.mateEtaUiModels.observe(this) {
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

    companion object {
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        fun getIntent(
            context: Context,
            meetingId: Long,
        ): Intent {
            return Intent(context, EtaDashboardActivity::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
            }
        }
    }
}
