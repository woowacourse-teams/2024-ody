package com.woowacourse.ody.presentation.room.log

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentNotificationLogBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.room.MeetingRoomActivity
import com.woowacourse.ody.presentation.room.MeetingRoomViewModel
import com.woowacourse.ody.presentation.room.log.adapter.MatesAdapter
import com.woowacourse.ody.presentation.room.log.adapter.NotificationLogsAdapter
import com.woowacourse.ody.presentation.room.log.listener.InviteCodeCopyListener
import com.woowacourse.ody.presentation.room.log.listener.MenuListener

class NotificationLogFragment :
    BindingFragment<FragmentNotificationLogBinding>(R.layout.fragment_notification_log),
    MenuListener,
    InviteCodeCopyListener {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val notificationLogsAdapter: NotificationLogsAdapter by lazy { NotificationLogsAdapter() }
    private val matesAdapter: MatesAdapter by lazy { MatesAdapter() }

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
        binding.backListener = requireActivity() as MeetingRoomActivity
        binding.menuListener = this
        binding.inviteCodeCopyListener = this
        binding.rvNotificationLog.adapter = notificationLogsAdapter
        binding.rvMates.adapter = matesAdapter
    }

    private fun initializeObserve() {
        viewModel.notificationLogs.observe(viewLifecycleOwner) {
            notificationLogsAdapter.submitList(it)
        }
        viewModel.mates.observe(viewLifecycleOwner) {
            matesAdapter.submitList(it)
        }
    }

    override fun onClickMenu() {
        binding.dlNotificationLog.openDrawer(GravityCompat.END)
    }

    override fun onCopyInviteCode() {
        val inviteCode = viewModel.meeting.value?.inviteCode
        viewModel.shareInviteCode(
            title = getString(R.string.invite_code_share_title),
            description = getString(R.string.invite_code_share_description, inviteCode),
            buttonTitle = getString(R.string.invite_code_share_button),
            imageUrl = INVITE_CODE_SHARE_IMAGE_URL,
        )
    }

    companion object {
        private const val INVITE_CODE_SHARE_IMAGE_URL =
            "https://firebasestorage.googleapis.com/v0/b/oddy-4482e.appspot.com" +
                "/o/happyody.png?alt=media&token=631fb9b9-a19d-418d-b0b4-426f9c64268f"
    }
}
