package com.mulberry.ody.presentation.feature.creation.model

enum class MeetingCreationType {
    NAME,
    DATE,
    TIME,
    DESTINATION,
    ;

    companion object {
        fun from(value: Int) = entries.first { it.ordinal == value }
    }
}
