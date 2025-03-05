package com.mulberry.ody.presentation.feature.creation.destination

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentMeetingDestinationBinding
import com.mulberry.ody.presentation.feature.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.common.collectWhenStarted
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.feature.creation.MeetingCreationInfoType
import com.mulberry.ody.presentation.feature.creation.MeetingCreationViewModel

class MeetingDestinationFragment :
    BindingFragment<FragmentMeetingDestinationBinding>(
        R.layout.fragment_meeting_destination,
    ) {
    private val viewModel: MeetingCreationViewModel by activityViewModels<MeetingCreationViewModel>()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeBinding()
        initializeObserve()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.addressSearchListener = activity as AddressSearchListener
    }

    private fun initializeObserve() {
        collectWhenStarted(viewModel.invalidDestinationEvent) {
            showSnackBar(R.string.invalid_address)
        }
        collectWhenStarted(viewModel.defaultLocationError) {
            showSnackBar(R.string.default_location_error_guide)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION
    }
}
