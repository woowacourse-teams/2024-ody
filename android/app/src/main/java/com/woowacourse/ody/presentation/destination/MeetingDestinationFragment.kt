package com.woowacourse.ody.presentation.destination

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.snackbar.Snackbar
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentMeetingDestinationBinding
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog
import com.woowacourse.ody.presentation.address.listener.AddressSearchListener
import com.woowacourse.ody.presentation.address.ui.GeoLocationUiModel
import com.woowacourse.ody.presentation.address.ui.toGeoLocation
import com.woowacourse.ody.presentation.meetinginfo.MeetingInfoViewModel

class MeetingDestinationFragment : Fragment(), AddressSearchListener {
    private var _binding: FragmentMeetingDestinationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingInfoViewModel by activityViewModels<MeetingInfoViewModel>()

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
        initializeBinding()
        initializeObserve()
        initializeView()
    }

    private fun initializeBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.addressSearchListener = this
    }

    private fun initializeObserve() {
        viewModel.isValidDestination.observe(viewLifecycleOwner) { isValid ->
            if (isValid) return@observe
            showSnackBar(R.string.invalid_address)
        }
    }

    private fun initializeView() {
        setFragmentResultListener(AddressSearchDialog.REQUEST_KEY) { _, bundle ->
            val geoLocation = bundle.getGeoLocation() ?: return@setFragmentResultListener
            binding.etDestination.setText(geoLocation.address)
            viewModel.setDestinationGeoLocation(geoLocation)
        }
    }

    private fun Bundle.getGeoLocation(): GeoLocation? {
        val geoLocationUiModel =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelable(AddressSearchDialog.GEO_LOCATION_UI_MODEL_KEY, GeoLocationUiModel::class.java)
            } else {
                getParcelable<GeoLocationUiModel>(AddressSearchDialog.GEO_LOCATION_UI_MODEL_KEY)
            }
        return geoLocationUiModel?.toGeoLocation()
    }

    override fun search() = AddressSearchDialog().show(parentFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)

    private fun showSnackBar(
        @StringRes messageId: Int
    ) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
