package com.mulberry.ody.presentation.room.log

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentNotificationLogBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.launchWhenStarted
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.room.log.adapter.MatesAdapter
import com.mulberry.ody.presentation.room.log.adapter.NotificationLogsAdapter
import com.mulberry.ody.presentation.room.log.listener.MenuListener
import com.mulberry.ody.presentation.room.log.listener.NotificationLogListener
import kotlinx.coroutines.launch

class NotificationLogFragment :
    BindingFragment<FragmentNotificationLogBinding>(R.layout.fragment_notification_log),
    MenuListener,
    NotificationLogListener {
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
        binding.notificationLogListener = this
        binding.rvNotificationLog.adapter = notificationLogsAdapter
        binding.rvMates.adapter = matesAdapter
    }

    private fun initializeObserve() {
        launchWhenStarted {
            launch {
                viewModel.notificationLogs.collect {
                    notificationLogsAdapter.submitList(it)
                }
            }
            launch {
                viewModel.mates.collect {
                    matesAdapter.submitList(it)
                }
            }
            launch {
                viewModel.copyInviteCodeEvent.collect {
                    val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                    intent.type = "text/plain"

                    val shareMessage = getString(R.string.invite_code_share, it.meetingName, it.inviteCode)
                    intent.putExtra(Intent.EXTRA_TEXT, shareMessage)

                    val chooserTitle = "친구에게 공유하기"
                    startActivity(Intent.createChooser(intent, chooserTitle))
                }
            }
        }
    }

    override fun onClickMenu() {
        binding.dlNotificationLog.openDrawer(GravityCompat.END)
    }

    override fun onCopyInviteCode() {
        viewModel.copyInviteCode()
    }

    override fun onExitMeetingRoom() {
        ExitMeetingRoomDialog().show(parentFragmentManager, EXIT_MEETING_ROOM_DIALOG_TAG)
    }

    companion object {
        private const val EXIT_MEETING_ROOM_DIALOG_TAG = "exitMeetingRoomDialog"
    }
}
