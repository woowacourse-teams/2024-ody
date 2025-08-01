package com.mulberry.ody.domain

import com.mulberry.ody.domain.model.MeetingName
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.lang.IllegalArgumentException

class MeetingNameTest {
    @ParameterizedTest
    @ValueSource(strings = ["약속이름1", "1", "약속이름약속이름약속이름123"])
    fun `약속 이름이 1자에서 15자인 경우 유효하다`(name: String) {
        assertDoesNotThrow {
            MeetingName(name = name)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "약속이름약속이름약속이름약속이름"])
    fun `약속 이름이 1자에서 15자가 아닌 경우 유효하지 않다`(name: String) {
        assertThrows<IllegalArgumentException> {
            MeetingName(name = name)
        }
    }
}
