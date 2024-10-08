package com.mulberry.ody.domain.validator

object AddressValidator {
    private val CAPITAL_REGIONS = listOf("서울", "경기", "인천")

    fun isValid(address: String): Boolean = CAPITAL_REGIONS.any { address.contains(it) }
}
