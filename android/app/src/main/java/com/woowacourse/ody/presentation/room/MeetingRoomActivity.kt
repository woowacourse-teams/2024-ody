package com.woowacourse.ody.presentation.room

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.ActivityMeetingRoomBinding
import com.woowacourse.ody.presentation.common.binding.BindingActivity
import com.woowacourse.ody.presentation.common.listener.BackListener
import com.woowacourse.ody.presentation.room.log.NotificationLogFragment
import com.woowacourse.ody.presentation.room.log.listener.CopyInviteCodeListener
import com.woowacourse.ody.presentation.room.log.listener.ShareListener

class MeetingRoomActivity :
    BindingActivity<ActivityMeetingRoomBinding>(R.layout.activity_meeting_room),
    CopyInviteCodeListener,
    ShareListener,
    BackListener {
    private val viewModel: MeetingRoomViewModel by viewModels<MeetingRoomViewModel> {
        MeetingRoomViewModelFactory(
            application.firebaseAnalytics,
            getMeetingId(),
            application.matesEtaRepository,
            application.notificationLogRepository,
            application.meetingRepository,
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
        binding.shareListener = this
        binding.copyInviteCodeListener = this
        binding.backListener = this
    }

    private fun initializeObserve() {
        viewModel.navigateToEtaEvent.observe(this) {
            // todo: eta 화면을 스택에 추가
        }
    }

    private fun initializePersistentBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)
    }

    override fun onCopyInviteCode() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val inviteCode = viewModel.meeting.value?.inviteCode
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(INVITE_CODE_LABEL, inviteCode)
        clipboard.setPrimaryClip(clip)
    }

    override fun onShare() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onBack() {
        finish()
    }

    private fun getMeetingId(): Long = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)

    companion object {
        private const val INVITE_CODE_LABEL = "inviteCode"
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        fun getIntent(
            context: Context,
            meetingId: Long,
        ): Intent {
            return Intent(context, NotificationLogFragment::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
            }
        }
    }
}
