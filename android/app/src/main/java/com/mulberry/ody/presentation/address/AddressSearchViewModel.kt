package com.mulberry.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.GeoLocation
import com.mulberry.ody.domain.repository.location.GeoLocationRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val locationRepository: GeoLocationRepository,
    ) : BaseViewModel() {
        private val _geoLocation: MutableLiveData<GeoLocation> = MutableLiveData<GeoLocation>()
        val geoLocation: LiveData<GeoLocation> get() = _geoLocation

        fun fetchGeoLocation(address: String) {
            viewModelScope.launch {
                startLoading()
                locationRepository.fetchGeoLocation(address)
                    .onSuccess {
                        _geoLocation.value = it
                    }
                    .onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }
                    .onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { fetchGeoLocation(address) }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "AddressSearchViewModel"
        }
    }
