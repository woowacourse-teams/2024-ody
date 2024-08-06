package com.woowacourse.ody.domain.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AddressValidatorTest {
    @Test
    fun `주소가 수도권인 경우 유효하다2`() {
        val actual = AddressValidator.isValid("인천광역시 남동구")
        assertThat(actual).isTrue
    }

    @ParameterizedTest
    @ValueSource(strings = ["인천광역시 남동구", "서울특별시 강남구", "경기도 수원시"])
    fun `주소가 수도권인 경우 유효하다`(address: String) {
        val actual = AddressValidator.isValid(address)
        assertThat(actual).isTrue
    }

    @ParameterizedTest
    @ValueSource(strings = ["부산광역시 남구", "광주광역시 동구"])
    fun `주소가 수도권이 아닌 경우 유효하지 않다`(address: String) {
        val actual = AddressValidator.isValid(address)
        assertThat(actual).isFalse
    }
}
