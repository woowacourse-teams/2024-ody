package com.mulberry.ody.data.remote.thirdparty.address.response

import com.mulberry.ody.domain.model.Address

fun AddressResponse.toAddresses(): List<Address> {
    return documents.map { it.toAddress() }
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
