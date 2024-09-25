package com.mulberry.ody.presentation.address

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.presentation.address.listener.AddressListener
import com.mulberry.ody.presentation.address.model.AddressUiModel
import com.mulberry.ody.presentation.address.model.toAddressUiModels
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
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
        private val addressRepository: AddressRepository,
    ) : BaseViewModel(), AddressListener {
        val addressSearchKeyword: MutableLiveData<String> = MutableLiveData()
        val hasAddressSearchKeyword: LiveData<Boolean> = addressSearchKeyword.map { it.isNotEmpty() }

        private val addresses: MutableLiveData<List<Address>> = MutableLiveData(emptyList())
        val isEmptyAddresses: LiveData<Boolean> = addresses.map { it.isEmpty() }
        val addressUiModels: LiveData<List<AddressUiModel>> = addresses.map { it.toAddressUiModels() }

        private val _addressSelectEvent: MutableSingleLiveData<Address> = MutableSingleLiveData()
        val addressSelectEvent: SingleLiveData<Address> get() = _addressSelectEvent

        fun clearAddressSearchKeyword() {
            addressSearchKeyword.value = ""
        }

        fun searchAddress() {
            val addressSearchKeyword = addressSearchKeyword.value ?: return
            if (addressSearchKeyword.isEmpty()) return

            viewModelScope.launch {
                startLoading()
                addressRepository.fetchAddresses(addressSearchKeyword, PAGE_SIZE)
                    .onSuccess {
                        addresses.value = it
                    }.onFailure { code, errorMessage ->
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { searchAddress() }
                    }
                stopLoading()
            }
        }

        override fun onClickAddressItem(id: Long) {
            val address = addresses.value?.find { it.id == id } ?: return
            _addressSelectEvent.setValue(address)
        }

        fun clearAddresses() {
            addresses.value = emptyList()
        }

        companion object {
            private const val TAG = "AddressSearchViewModel"
            private const val PAGE_SIZE = 10
        }
    }
