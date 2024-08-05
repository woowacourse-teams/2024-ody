package com.woowacourse.ody.presentation.room.log

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityNotificationLogBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.room.log.adapter.NotificationLogsAdapter
import com.woowacourse.ody.presentation.room.log.listener.CopyInviteCodeListener
import com.woowacourse.ody.presentation.room.log.listener.ShareListener

class NotificationLogActivity :
    BindingActivity<ActivityNotificationLogBinding>(
        R.layout.activity_notification_log,
    ),
    CopyInviteCodeListener,
    ShareListener,
    BackListener {
    private val viewModel: NotificationLogViewModel by viewModels {
        NotificationLogViewModelFactory(
            application.notificationLogRepository,
            application.meetingRepository,
        )
    }

    private val adapter: NotificationLogsAdapter by lazy {
        NotificationLogsAdapter(
            this,
        )
    }

    private val bottomSheetLayout by lazy { findViewById<ConstraintLayout>(R.id.cl_bottom_sheet) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeBinding()
        initializeObserve()
        initializePersistentBottomSheet()
    }

    override fun initializeBinding() {
        binding.vm = viewModel
        binding.rvNotificationLog.adapter = adapter
        binding.shareListener = this
        binding.copyInviteCodeListener = this
        binding.backListener = this
    }

    private fun initializeObserve() {
        viewModel.notificationLogs.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
    }

    override fun onShare() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCopyInviteCode() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val inviteCode = viewModel.meeting.value?.inviteCode
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(INVITE_CODE_LABEL, inviteCode)
        clipboard.setPrimaryClip(clip)
    }

    override fun onBack() {
        finish()
    }

    companion object {
        private const val INVITE_CODE_LABEL = "inviteCode"

        fun getIntent(context: Context): Intent = Intent(context, NotificationLogActivity::class.java)
    }
}
