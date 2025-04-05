package com.mulberry.ody.presentation.feature.address.listener

import com.mulberry.ody.domain.model.Address

interface AddressSearchListener {
    fun onSearch()

    fun onReceive(address: Address)
}
