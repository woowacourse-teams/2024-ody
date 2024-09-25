package com.mulberry.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.model.Location
import com.mulberry.ody.domain.repository.location.GeoLocationRepository
import com.mulberry.ody.presentation.address.listener.LocationListener
import com.mulberry.ody.presentation.address.model.AddressUiModel
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
    ) : BaseViewModel(), LocationListener {
        val addressSearchKeyword: MutableLiveData<String> = MutableLiveData()
        val hasAddressSearchKeyword: LiveData<Boolean> = addressSearchKeyword.map { it.isNotEmpty() }

        private val _addresses: MutableLiveData<List<Location>> = MutableLiveData(emptyList())

        private val _addressUiModels: MutableLiveData<List<AddressUiModel>> = MutableLiveData(emptyList())
        val addressUiModels: LiveData<List<AddressUiModel>> get() = _addressUiModels

        val isEmptyAddresses: LiveData<Boolean> = _addressUiModels.map { it.isEmpty() }

        private val _locationSelectEvent: MutableSingleLiveData<Location> = MutableSingleLiveData()
        val locationSelectEvent: SingleLiveData<Location> get() = _locationSelectEvent

        fun clearAddressSearchKeyword() {
            addressSearchKeyword.value = ""
        }

        fun searchLocation() {
            val addressSearchKeyword = addressSearchKeyword.value ?: return
            viewModelScope.launch {
                startLoading()
                _addressUiModels.value =
                        listOf(
                            AddressUiModel(id = 100, name = "테스트1", "도로명주소1"),
                            AddressUiModel(id = 101, name = "테스트2", "도로명주소2"),
                            AddressUiModel(id = 102, name = "테스트3", "도로명주소3"),
                            AddressUiModel(id = 103, name = "테스트4", "도로명주소4"),
                        )
                // 카카오에서 addressSearchKeyword으로 결과 가져오기
                stopLoading()
            }
        }

        override fun onClickLocationItem(id: Long) {
            val address = _addresses.value?.find { it.id == id } ?: return
            _locationSelectEvent.setValue(address)
        }

        companion object {
            private const val TAG = "AddressSearchViewModel"
        }
}
