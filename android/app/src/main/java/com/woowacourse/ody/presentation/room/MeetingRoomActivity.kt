package com.woowacourse.ody.presentation.room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingRoomBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.room.adapter.NotificationLogsAdapter
import com.woowacourse.ody.presentation.room.listener.CopyInviteCodeListener
import com.woowacourse.ody.presentation.room.listener.ShareListener

class MeetingRoomActivity :
    BindingActivity<ActivityMeetingRoomBinding>(
        R.layout.activity_meeting_room,
    ),
    CopyInviteCodeListener,
    ShareListener {
    private val viewModel: MeetingRoomViewModel by viewModels {
        MeetingRoomViewModelFactory(
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

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.rvNotificationLog.adapter = adapter
        binding.rvNotificationLog.layoutManager = LinearLayoutManager(this)
        binding.shareListener = this
        binding.copyInviteCodeListener = this
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

    companion object {
        private const val INVITE_CODE_LABEL = "inviteCode"

        fun getIntent(context: Context): Intent = Intent(context, MeetingRoomActivity::class.java)
    }
}
