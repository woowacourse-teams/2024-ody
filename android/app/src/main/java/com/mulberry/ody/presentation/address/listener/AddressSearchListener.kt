package com.mulberry.ody.presentation.address.listener

import com.mulberry.ody.domain.model.GeoLocation

interface AddressSearchListener {
    fun onSearch()

    fun onReceive(geoLocation: GeoLocation)
}
