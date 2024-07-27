package com.woowacourse.ody.presentation.departure

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
import com.woowacourse.ody.databinding.FragmentDepartureBinding
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog
import com.woowacourse.ody.presentation.address.listener.AddressSearchListener
import com.woowacourse.ody.presentation.address.model.GeoLocationUiModel
import com.woowacourse.ody.presentation.address.model.toGeoLocation
import com.woowacourse.ody.presentation.meeting.MeetingInfoType
import com.woowacourse.ody.presentation.meeting.MeetingInfoViewModel
import com.woowacourse.ody.presentation.common.observeEvent

class DepartureFragment : Fragment(), AddressSearchListener {
    private var _binding: FragmentDepartureBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MeetingInfoViewModel by activityViewModels<MeetingInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDepartureBinding.inflate(inflater, container, false)
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
        viewModel.invalidStartingPointEvent.observeEvent(viewLifecycleOwner) {
            showSnackBar(R.string.invalid_address)
        }
    }

    private fun initializeView() {
        setFragmentResultListener(AddressSearchDialog.REQUEST_KEY) { _, bundle ->
            val geoLocation = bundle.getGeoLocation() ?: return@setFragmentResultListener
            binding.etStartingPoint.setText(geoLocation.address)
            viewModel.startingPointGeoLocation.value = geoLocation
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
        @StringRes messageId: Int,
    ) {
        Snackbar.make(binding.root, messageId, Snackbar.LENGTH_SHORT)
            .apply { setAnchorView(activity?.findViewById(R.id.btn_next)) }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.meetingInfoType.value = MeetingInfoType.STARTING_POINT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
