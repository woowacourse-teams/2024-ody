package com.woowacourse.ody.presentation.destination

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.woowacourse.ody.databinding.FragmentMeetingDestinationBinding
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog

class MeetingDestinationFragment : Fragment() {
    private var _binding: FragmentMeetingDestinationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMeetingDestinationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()
    }

    private fun initializeView() {
        binding.etDestination.setOnClickListener {
            AddressSearchDialog().show(parentFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)
        }
        setFragmentResultListener(AddressSearchDialog.REQUEST_KEY) { _, bundle ->
            val geoLocation = bundle.getGeoLocation() ?: return@setFragmentResultListener
            binding.etDestination.setText(geoLocation.address)
        }
    }

    private fun Bundle.getGeoLocation(): GeoLocation? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getParcelable(AddressSearchDialog.GEO_LOCATION_KEY, GeoLocation::class.java)
        } else {
            getParcelable<GeoLocation>(AddressSearchDialog.GEO_LOCATION_KEY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
