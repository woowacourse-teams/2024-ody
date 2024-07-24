package com.woowacourse.ody.presentation.startingpoint

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.woowacourse.ody.databinding.FragmentJoinStartingPointBinding
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressSearchDialog
import com.woowacourse.ody.presentation.address.ui.GeoLocationUiModel
import com.woowacourse.ody.presentation.address.ui.toGeoLocation

class JoinStartingPointFragment : Fragment() {
    private var _binding: FragmentJoinStartingPointBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentJoinStartingPointBinding.inflate(inflater, container, false)
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
        binding.etStartingPoint.setOnClickListener {
            AddressSearchDialog().show(parentFragmentManager, ADDRESS_SEARCH_DIALOG_TAG)
        }
        setFragmentResultListener(AddressSearchDialog.REQUEST_KEY) { _, bundle ->
            val geoLocation = bundle.getGeoLocation() ?: return@setFragmentResultListener
            binding.etStartingPoint.setText(geoLocation.address)
        }
    }

    private fun Bundle.getGeoLocation(): GeoLocation? {
        val geoLocationUiModel =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getParcelable(
                    AddressSearchDialog.GEO_LOCATION_UI_MODEL_KEY,
                    GeoLocationUiModel::class.java
                )
            } else {
                getParcelable<GeoLocationUiModel>(AddressSearchDialog.GEO_LOCATION_UI_MODEL_KEY)
            }
        return geoLocationUiModel?.toGeoLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ADDRESS_SEARCH_DIALOG_TAG = "address_search_dialog"
    }
}
