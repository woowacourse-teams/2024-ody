package com.mulberry.ody.presentation.feature.creation.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentMeetingNameBinding
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.feature.creation.MeetingCreationInfoType
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel

class MeetingNameFragment : BindingFragment<FragmentMeetingNameBinding>(R.layout.fragment_meeting_name) {
    private val viewModel: MeetingCreationViewModel by activityViewModels<MeetingCreationViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.NAME
    }
}
