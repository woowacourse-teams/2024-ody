package com.mulberry.ody.presentation.address

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mulberry.ody.data.remote.thirdparty.address.AddressPagingSource
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.presentation.address.listener.AddressListener
import com.mulberry.ody.presentation.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressSearchViewModel
    @Inject
    constructor(
        private val addressRepository: AddressRepository,
    ) : BaseViewModel(), AddressListener {
        val addressSearchKeyword: MutableStateFlow<String> = MutableStateFlow("")
        val hasAddressSearchKeyword: StateFlow<Boolean> =
            addressSearchKeyword.map { it.isNotEmpty() }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                initialValue = false,
            )

        private val _address: MutableStateFlow<PagingData<Address>> = MutableStateFlow(PagingData.empty())
        val address: StateFlow<PagingData<Address>> get() = _address

        private val _isEmptyAddresses: MutableStateFlow<Boolean> = MutableStateFlow(true)
        val isEmptyAddresses: StateFlow<Boolean> get() = _isEmptyAddresses

        private val _addressSelectEvent: MutableSharedFlow<Address> = MutableSharedFlow()
        val addressSelectEvent: SharedFlow<Address> get() = _addressSelectEvent.asSharedFlow()

        private val _addressClearEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val addressClearEvent: SharedFlow<Unit> get() = _addressClearEvent.asSharedFlow()

        fun clearAddressSearchKeyword() {
            addressSearchKeyword.value = ""
        }

        fun searchAddress() {
            val addressSearchKeyword = addressSearchKeyword.value
            if (addressSearchKeyword.isEmpty()) return

            viewModelScope.launch {
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
                    .collectLatest { _address.emit(it) }
                stopLoading()
            }
        }

        override fun onClickAddressItem(address: Address) {
            viewModelScope.launch {
                _addressSelectEvent.emit(address)
            }
        }

        fun updateAddressItemCount(itemCount: Int) {
            viewModelScope.launch {
                _isEmptyAddresses.emit(itemCount == 0)
            }
        }

        fun clearAddresses() {
            viewModelScope.launch {
                _addressClearEvent.emit(Unit)
            }
        }

        companion object {
            private const val PAGE_SIZE = 10
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
        }
    }
