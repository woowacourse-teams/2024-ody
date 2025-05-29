package com.mulberry.ody.presentation.feature.address

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.usecase.SearchAddressUseCase
import com.mulberry.ody.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val searchAddressUseCase: SearchAddressUseCase,
    ) : BaseViewModel() {
        private val _address: MutableStateFlow<PagingData<Address>> = MutableStateFlow(PagingData.empty())
        val address: StateFlow<PagingData<Address>> get() = _address

        fun searchAddress(addressSearchKeyword: String) {
            viewModelScope.launch {
                if (addressSearchKeyword.isBlank()) {
                    return@launch
                }

                startLoading()
                searchAddressUseCase(addressSearchKeyword)
                    .cachedIn(viewModelScope)
                    .collect {
                        _address.emit(it)
                        stopLoading()
                    }
            }
        }

        fun clearAddresses() {
            viewModelScope.launch {
                _address.emit(PagingData.empty())
            }
        }

        fun handleAddressPageError(
            addressSearchKeyword: String,
            throwable: Throwable,
        ) {
            if (throwable is IOException) {
                handleNetworkError()
                lastFailedAction = { searchAddress(addressSearchKeyword) }
            } else {
                handleError()
            }
        }
    }
