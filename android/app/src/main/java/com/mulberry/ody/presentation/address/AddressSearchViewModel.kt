package com.mulberry.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.presentation.address.listener.AddressListener
import com.mulberry.ody.presentation.address.model.AddressUiModel
import com.mulberry.ody.presentation.address.model.toAddressUiModels
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
    ) : BaseViewModel(), AddressListener {
        val addressSearchKeyword: MutableLiveData<String> = MutableLiveData()
        val hasAddressSearchKeyword: LiveData<Boolean> = addressSearchKeyword.map { it.isNotEmpty() }

        private val _addresses: MutableLiveData<List<Address>> = MutableLiveData(emptyList())
        val isEmptyAddresses: LiveData<Boolean> = _addresses.map { it.isEmpty() }
        val addressUiModels: LiveData<List<AddressUiModel>> = _addresses.map { it.toAddressUiModels() }

        private val _addressSelectEvent: MutableSingleLiveData<Address> = MutableSingleLiveData()
        val addressSelectEvent: SingleLiveData<Address> get() = _addressSelectEvent

        fun clearAddressSearchKeyword() {
            addressSearchKeyword.value = ""
        }

        fun searchAddress() {
            val addressSearchKeyword = addressSearchKeyword.value ?: return
            viewModelScope.launch {
                startLoading()
                _addresses.value =
                        listOf(
                            Address(id = 100, name = "테스트1", "도로명주소1", "1.1", "1.1"),
                            Address(id = 101, name = "테스트2", "도로명주소2", "1.1", "1.1"),
                            Address(id = 102, name = "테스트3", "도로명주소3", "1.1", "1.1"),
                            Address(id = 103, name = "테스트4", "도로명주소4", "1.1", "1.1"),
                        )
                // 카카오에서 addressSearchKeyword으로 결과 가져오기
                stopLoading()
            }
        }

        override fun onClickAddressItem(id: Long) {
            val address = _addresses.value?.find { it.id == id } ?: return
            _addressSelectEvent.setValue(address)
        }

        companion object {
            private const val TAG = "AddressSearchViewModel"
        }
}
