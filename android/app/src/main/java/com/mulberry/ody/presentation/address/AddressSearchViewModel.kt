package com.mulberry.ody.presentation.address

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
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
        val addressSearchKeyword: MutableStateFlow<String> = MutableStateFlow("")
        val hasAddressSearchKeyword: StateFlow<Boolean> =
            addressSearchKeyword.map { it.isNotEmpty() }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = false,
            )

        private val addresses: MutableStateFlow<List<Address>> = MutableStateFlow(emptyList())
        val isEmptyAddresses: StateFlow<Boolean> =
            addresses.map { it.isEmpty() }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = true,
            )
        val addressUiModels: StateFlow<List<AddressUiModel>> =
            addresses.map { it.toAddressUiModels() }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

        private val _addressSelectEvent: MutableSharedFlow<Address> = MutableSharedFlow()
        val addressSelectEvent: SharedFlow<Address> get() = _addressSelectEvent.asSharedFlow()

        fun clearAddressSearchKeyword() {
            addressSearchKeyword.value = ""
        }

        fun searchAddress() {
            val addressSearchKeyword = addressSearchKeyword.value
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
            viewModelScope.launch {
                _addressSelectEvent.emit(address)
            }
        }

        fun clearAddresses() {
            addresses.value = emptyList()
        }

        companion object {
            private const val TAG = "AddressSearchViewModel"
            private const val PAGE_SIZE = 10
        }
    }
