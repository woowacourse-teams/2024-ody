package com.woowacourse.ody.presentation.address.listener

import com.woowacourse.ody.domain.model.GeoLocation

interface AddressSearchListener {
    fun onSearch()

    fun onReceive(geoLocation: GeoLocation)
}
