package com.mulberry.ody.presentation.feature.creation.time

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentMeetingTimeBinding
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.feature.creation.MeetingCreationInfoType
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel

class MeetingTimeFragment :
    BindingFragment<FragmentMeetingTimeBinding>(R.layout.fragment_meeting_time) {
    private val viewModel: MeetingCreationViewModel by activityViewModels<MeetingCreationViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        initializeObserve()
        initializeView()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.invalidMeetingTimeEvent) {
            showSnackBar(R.string.invalid_meeting_time)
        }
    }

    private fun initializeView() {
        binding.npMeetingTimeHour.wrapSelectorWheel = true
        binding.npMeetingTimeMinute.wrapSelectorWheel = true
    }

    override fun onResume() {
        super.onResume()
        viewModel.initializeMeetingTime()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.TIME
    }
}
