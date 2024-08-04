package com.woowacourse.ody.presentation.creation.name

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingNameBinding
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.creation.MeetingCreationInfoType
import com.woowacourse.ody.presentation.creation.MeetingCreationViewModel

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
