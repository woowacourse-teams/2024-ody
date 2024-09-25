package com.mulberry.ody.presentation.address.listener

import com.mulberry.ody.domain.model.Location

interface AddressSearchListener {
    fun onSearch()

    fun onReceive(location: Location)
}
