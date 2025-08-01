package com.mulberry.ody.domain

import com.mulberry.ody.domain.model.MeetingDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

class MeetingDateTimeTest {
    @Test
    fun `약속 날짜와 시간이 현재보다 이후인 경우 유효하다`() {
        assertDoesNotThrow {
            MeetingDateTime(dateTime = LocalDateTime.now().plusDays(10))
        }
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이전인 경우 유효하지 않다`() {
        assertThrows<IllegalArgumentException> {
            MeetingDateTime(dateTime = LocalDateTime.now().minusDays(10))
        }
    }

    @Test
    fun `약속 날짜와 시간이 현재와 같은 경우 유효하지 않다`() {
        assertThrows<IllegalArgumentException> {
            MeetingDateTime(dateTime = LocalDateTime.now())
        }
    }
}
