package com.mulberry.ody.domain.model

class MeetingName(val name: String) {
    init {
        require(name.length in LENGTH_RANGE) { "약속 이름은 $MIN_LENGTH~${MAX_LENGTH}자 사이여야 합니다." }
    }

    companion object {
        private const val MIN_LENGTH = 1
        private const val MAX_LENGTH = 15
        private val LENGTH_RANGE = MIN_LENGTH..MAX_LENGTH
    }
}
