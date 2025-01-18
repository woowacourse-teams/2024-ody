package com.mulberry.ody.presentation.room.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentDetailMeetingBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.room.MeetingRoomActivity
import com.mulberry.ody.presentation.room.MeetingRoomViewModel

class DetailMeetingFragment :
    BindingFragment<FragmentDetailMeetingBinding>(R.layout.fragment_detail_meeting) {
    private val viewModel: MeetingRoomViewModel by activityViewModels<MeetingRoomViewModel>()

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
    }

    private fun initializeObserve() {
    }
}
