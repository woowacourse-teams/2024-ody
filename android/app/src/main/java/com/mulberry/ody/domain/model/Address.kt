package com.mulberry.ody.domain.model

import kotlinx.serialization.Serializable

@Serializable
class Address(
    val id: Long = -1,
    val placeName: String = "",
    val detailAddress: String,
    val longitude: String,
    val latitude: String,
) {
    fun isValid(): Boolean {
        return CAPITAL_REGIONS.any { detailAddress.contains(it) }
    }

    companion object {
        private val CAPITAL_REGIONS = listOf("서울", "경기", "인천")
    }
}
