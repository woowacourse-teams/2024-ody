package com.ydo.ody.presentation.creation.time

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.ydo.ody.R
import com.ydo.ody.databinding.FragmentMeetingTimeBinding
import com.ydo.ody.presentation.common.binding.BindingFragment
import com.ydo.ody.presentation.creation.MeetingCreationInfoType
import com.ydo.ody.presentation.creation.MeetingCreationViewModel

class MeetingTimeFragment : BindingFragment<FragmentMeetingTimeBinding>(R.layout.fragment_meeting_time) {
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
        viewModel.invalidMeetingTimeEvent.observe(viewLifecycleOwner) {
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
