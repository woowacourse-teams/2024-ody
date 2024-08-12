package com.woowacourse.ody.presentation.room.log

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentNotificationLogBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.room.MeetingRoomActivity
import com.woowacourse.ody.presentation.room.MeetingRoomViewModel
import com.woowacourse.ody.presentation.room.log.adapter.NotificationLogsAdapter

class NotificationLogFragment :
    BindingFragment<FragmentNotificationLogBinding>(R.layout.fragment_notification_log) {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()
    private val adapter: NotificationLogsAdapter by lazy {
        NotificationLogsAdapter(requireActivity() as MeetingRoomActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    fun initializeBinding() {
        binding.vm = viewModel
        binding.rvNotificationLog.adapter = adapter
    }

    private fun initializeObserve() {
        viewModel.notificationLogs.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }
}
