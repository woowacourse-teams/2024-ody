package com.woowacourse.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.domain.GeoLocationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class AddressSearchViewModel(
    private val coroutineScope: CoroutineScope,
    private val locationRepository: GeoLocationRepository,
) : ViewModel() {
    private val _geoLocation: MutableLiveData<GeoLocation> = MutableLiveData<GeoLocation>()
    val geoLocation: LiveData<GeoLocation> get() = _geoLocation

    fun fetchGeoLocation(address: String) {
        coroutineScope.launch {
            locationRepository.fetchGeoLocation(address)
                .onSuccess {
                    _geoLocation.value = it
                }.onFailure {
                    Timber.e(it.message)
                }
        }
    }
}
