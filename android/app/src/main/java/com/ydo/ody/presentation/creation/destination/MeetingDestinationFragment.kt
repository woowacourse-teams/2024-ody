package com.ydo.ody.presentation.creation.destination

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.ydo.ody.R
import com.ydo.ody.databinding.FragmentMeetingDestinationBinding
import com.ydo.ody.domain.model.GeoLocation
import com.ydo.ody.presentation.address.AddressSearchDialog
import com.ydo.ody.presentation.address.listener.AddressSearchListener
import com.ydo.ody.presentation.common.binding.BindingFragment
import com.ydo.ody.presentation.creation.MeetingCreationInfoType
import com.ydo.ody.presentation.creation.MeetingCreationViewModel

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

    override fun onSearch() = AddressSearchDialog().show(childFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)

    override fun onReceive(geoLocation: GeoLocation) {
        viewModel.destinationGeoLocation.value = geoLocation
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
