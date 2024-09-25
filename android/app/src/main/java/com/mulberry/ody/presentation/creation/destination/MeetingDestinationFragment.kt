package com.mulberry.ody.presentation.creation.destination

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.mulberry.ody.R
import com.mulberry.ody.databinding.FragmentMeetingDestinationBinding
import com.mulberry.ody.domain.model.Location
import com.mulberry.ody.presentation.address.AddressSearchFragment
import com.mulberry.ody.presentation.address.listener.AddressSearchListener
import com.mulberry.ody.presentation.common.binding.BindingFragment
import com.mulberry.ody.presentation.creation.MeetingCreationInfoType
import com.mulberry.ody.presentation.creation.MeetingCreationViewModel

class MeetingDestinationFragment :
    BindingFragment<FragmentMeetingDestinationBinding>(
        R.layout.fragment_meeting_destination,
    ),
    AddressSearchListener {
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
        binding.addressSearchListener = this
    }

    private fun initializeObserve() {
        viewModel.invalidDestinationEvent.observe(viewLifecycleOwner) {
            showSnackBar(R.string.invalid_address)
        }
    }

    override fun onSearch() {
        parentFragmentManager.commit {
            add(R.id.fcv_creation, AddressSearchFragment())
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onReceive(location: Location) {
        viewModel.destinationLocation.value = location
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION
    }
}
