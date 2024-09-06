package com.ydo.ody.presentation.address.listener

import com.ydo.ody.domain.model.GeoLocation

interface AddressSearchListener {
    fun onSearch()

    fun onReceive(geoLocation: GeoLocation)
}
