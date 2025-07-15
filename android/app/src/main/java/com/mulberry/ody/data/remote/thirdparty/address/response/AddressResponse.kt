package com.mulberry.ody.data.remote.thirdparty.address.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddressResponse(
    @SerialName("documents")
    val documents: List<Document>,
    @SerialName("meta")
    val meta: Meta,
)
