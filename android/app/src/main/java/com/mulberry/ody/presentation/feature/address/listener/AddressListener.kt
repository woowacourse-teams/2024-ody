package com.mulberry.ody.presentation.feature.address.listener

import com.mulberry.ody.domain.model.Address

interface AddressListener {
    fun onClickAddressItem(address: Address)
}
