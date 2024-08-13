package com.woowacourse.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.repository.location.GeoLocationRepository
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressSearchViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val locationRepository: GeoLocationRepository,
) : ViewModel() {
    private val _geoLocation: MutableLiveData<GeoLocation> = MutableLiveData<GeoLocation>()
    val geoLocation: LiveData<GeoLocation> get() = _geoLocation

    fun fetchGeoLocation(address: String) {
        viewModelScope.launch {
            locationRepository.fetchGeoLocation(address)
                .onSuccess {
                    _geoLocation.value = it
                }.onFailure {
                    analyticsHelper.logNetworkErrorEvent(TAG, it.message)
                    Timber.e(it.message)
                }
        }
    }

    companion object {
        private const val TAG = "AddressSearchViewModel"
    }
}
