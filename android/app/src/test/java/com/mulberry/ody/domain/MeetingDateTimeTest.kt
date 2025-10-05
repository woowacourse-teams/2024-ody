package com.mulberry.ody.domain

import com.mulberry.ody.domain.model.MeetingDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class MeetingDateTimeTest {
    @Test
    fun `약속 날짜와 시간이 현재보다 이후인 경우 유효하다`() {
        // given
        val datetime = MeetingDateTime(dateTime = LocalDateTime.now().plusDays(10))

        // when
        val actual = datetime.isValid()

        // then
        assertThat(actual).isEqualTo(true)
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이전인 경우 유효하지 않다`() {
        // given
        val datetime = MeetingDateTime(dateTime = LocalDateTime.now().minusDays(10))

        // when
        val actual = datetime.isValid()

        // then
        assertThat(actual).isEqualTo(false)
    }

    @Test
    fun `약속 날짜와 시간이 현재와 같은 경우 유효하지 않다`() {
        // given
        val datetime = MeetingDateTime(dateTime = LocalDateTime.now())

        // when
        val actual = datetime.isValid()

        // then
        assertThat(actual).isEqualTo(false)
    }
}
