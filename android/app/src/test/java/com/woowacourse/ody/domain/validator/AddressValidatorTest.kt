package com.woowacourse.ody.domain.validator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AddressValidatorTest {
    @Test
    fun `주소가 수도권인 경우 유효하다`() {
        // given
        val address = "인천광역시 남동구"

        // when
        val actual = AddressValidator.isValid(address)

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `주소가 수도권이 아닌 경우 유효하지 않다`() {
        // given
        val address = "부산광역시 남구"

        // when
        val actual = AddressValidator.isValid(address)

        // then
        assertThat(actual).isFalse
    }
}
