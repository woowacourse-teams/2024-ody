package com.mulberry.ody.presentation.room.log

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentNotificationLogBinding
import com.mulberry.ody.presentation.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomViewModel
import com.mulberry.ody.presentation.room.log.adapter.NotificationLogsAdapter

class NotificationLogFragment :
    BindingFragment<FragmentNotificationLogBinding>(R.layout.fragment_notification_log) {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val notificationLogsAdapter: NotificationLogsAdapter by lazy { NotificationLogsAdapter() }

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
        binding.meetingRoomListener = requireActivity() as MeetingRoomActivity
        binding.rvNotificationLog.adapter = notificationLogsAdapter
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.notificationLogs) {
            notificationLogsAdapter.submitList(it)
        }
    }
}
