package com.woowacourse.ody.domain

enum class NotificationType {
    ENTRY,
    DEFAULT,
    ;

    companion object {
        fun from(tag: String): NotificationType =
            when (tag) {
                "Entry" -> ENTRY
                else -> DEFAULT
            }
    }
}
