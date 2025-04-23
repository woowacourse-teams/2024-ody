package com.mulberry.ody.presentation.feature.address

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mulberry.ody.data.remote.thirdparty.address.AddressPagingSource
import com.mulberry.ody.data.remote.thirdparty.address.AddressPagingSource.Companion.PAGE_SIZE
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val addressRepository: AddressRepository,
    ) : BaseViewModel() {
        private val _address: MutableStateFlow<PagingData<Address>> = MutableStateFlow(PagingData.empty())
        val address: StateFlow<PagingData<Address>> get() = _address

        fun searchAddress(addressSearchKeyword: String) {
            viewModelScope.launch {
                if (addressSearchKeyword.isBlank()) {
                    return@launch
                }

                startLoading()
                Pager(
                    config = PagingConfig(pageSize = PAGE_SIZE),
                    pagingSourceFactory = {
                        AddressPagingSource(
                            keyword = addressSearchKeyword,
                            addressRepository = addressRepository,
                        )
                    },
                ).flow
                    .cachedIn(viewModelScope)
                    .collectLatest {
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
