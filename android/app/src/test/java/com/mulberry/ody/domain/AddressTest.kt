package com.mulberry.ody.domain

import com.mulberry.ody.domain.model.Address
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AddressTest {
    @Test
    fun `주소가 수도권인 경우 유효하다`() {
        // given
        val address = Address(id = 1L, detailAddress = "인천광역시 남동구", longitude = "0.0", latitude = "0.0")

        // when
        val actual = address.isValid()

        // then
        assertThat(actual).isTrue
    }

    @Test
    fun `주소가 수도권이 아닌 경우 유효하지 않다`() {
        // given
        val address = Address(id = 1L, detailAddress = "부산광역시 남구", longitude = "0.0", latitude = "0.0")

        // when
        val actual = address.isValid()

        // then
        assertThat(actual).isFalse
    }
}
