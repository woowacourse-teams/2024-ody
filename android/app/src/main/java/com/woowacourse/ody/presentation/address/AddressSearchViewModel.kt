package com.woowacourse.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.GeoLocation
import com.woowacourse.ody.domain.GeoLocationRepository
import kotlinx.coroutines.launch

class AddressSearchViewModel(
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

                }
        }
    }
}
