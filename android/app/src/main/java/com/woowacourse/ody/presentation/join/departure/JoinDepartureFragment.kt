package com.woowacourse.ody.presentation.join.departure

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import com.woowacourse.ody.R
import com.woowacourse.ody.databinding.FragmentJoinDepartureBinding
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog
import com.woowacourse.ody.presentation.address.listener.AddressSearchListener
import com.woowacourse.ody.presentation.address.model.GeoLocationUiModel
import com.woowacourse.ody.presentation.address.model.toGeoLocation
import com.woowacourse.ody.presentation.common.binding.BindingFragment
import com.woowacourse.ody.presentation.join.MeetingJoinInfoType
import com.woowacourse.ody.presentation.join.MeetingJoinViewModel

class JoinDepartureFragment : BindingFragment<FragmentJoinDepartureBinding>(R.layout.fragment_join_departure), AddressSearchListener {
    private val viewModel: MeetingJoinViewModel by activityViewModels<MeetingJoinViewModel>()

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
        binding.addressSearchListener = this
    }

    private fun initializeObserve() {
        viewModel.invalidStartingPointEvent.observe(viewLifecycleOwner) {
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

    override fun onSearch() = AddressSearchDialog().show(parentFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)

    override fun onResume() {
        super.onResume()
        viewModel.meetingJoinInfoType.value = MeetingJoinInfoType.DEPARTURE
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
