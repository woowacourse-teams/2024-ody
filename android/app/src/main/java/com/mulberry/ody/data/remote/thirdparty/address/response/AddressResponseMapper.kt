package com.mulberry.ody.data.remote.thirdparty.address.response

import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.Addresses

fun AddressResponse.toAddresses(): Addresses {
    val addresses = documents.map { it.toAddress() }
    return Addresses(addresses, meta.isEnd)
}

private fun Document.toAddress(): Address {
    return Address(
        id = id.toLong(),
        placeName = placeName,
        detailAddress = roadAddressName.ifEmpty { addressName },
        longitude = x,
        latitude = y,
    )
}
