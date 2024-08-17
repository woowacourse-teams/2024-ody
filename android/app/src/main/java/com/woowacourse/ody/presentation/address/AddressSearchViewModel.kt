package com.woowacourse.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.repository.location.GeoLocationRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
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

    private val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    private val _errorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val errorEvent: SingleLiveData<Unit> get() = _errorEvent

    private var lastFailedAction: (() -> Unit)? = null

    fun fetchGeoLocation(address: String) {
        viewModelScope.launch {
            locationRepository.fetchGeoLocation(address)
                .onSuccess {
                    _geoLocation.value = it
                }
                .onFailure { code, errorMessage ->
                    _errorEvent.setValue(Unit)
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }
                .onNetworkError {
                    _networkErrorEvent.setValue(Unit)
                    lastFailedAction = { fetchGeoLocation(address) }
                }
        }
    }

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    companion object {
        private const val TAG = "AddressSearchViewModel"
    }
}
