package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressValidator

class MeetingInfoViewModel : ViewModel() {
    private val _destinationGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val destinationGeoLocation: LiveData<GeoLocation> get() = _destinationGeoLocation

    val isValidDestination: LiveData<Boolean> = _destinationGeoLocation.map { AddressValidator.isValid(it.address) }

    fun setDestinationGeoLocation(geoLocation: GeoLocation) {
        _destinationGeoLocation.value = geoLocation
    }
}
