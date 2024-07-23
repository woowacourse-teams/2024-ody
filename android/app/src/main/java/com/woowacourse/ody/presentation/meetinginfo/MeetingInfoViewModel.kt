package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.ViewModel

class MeetingInfoViewModel : ViewModel() {
    val hours: List<String> by lazy { MEETING_HOUR_RANGE.toStrings() }
    val minutes: List<String> by lazy { MEETING_MINUTE_RANGE.toStrings() }

    private fun IntRange.toStrings() = map {
        val string = it.toString()
        if (string.length == 1) "0$string" else string
    }

    companion object {
        private val MEETING_HOUR_RANGE = 0..23
        private val MEETING_MINUTE_RANGE = 0..59
    }
}
