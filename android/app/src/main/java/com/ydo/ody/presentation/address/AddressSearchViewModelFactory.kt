package com.ydo.ody.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ydo.ody.domain.repository.location.GeoLocationRepository
import com.ydo.ody.presentation.common.analytics.AnalyticsHelper

class AddressSearchViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val geoLocationRepository: GeoLocationRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddressSearchViewModel::class.java)) {
            return AddressSearchViewModel(analyticsHelper, geoLocationRepository) as T
        }
        throw IllegalArgumentException()
    }
}
