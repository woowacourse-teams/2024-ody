package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit
import java.time.LocalTime

class MeetingInfoViewModel : ViewModel() {
    private val _meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingHour: LiveData<Int> get() = _meetingHour

    private val _meetingMinute: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: LiveData<Int> get() = _meetingMinute

    private val _isValidMeetingTime: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isValidMeetingTime: LiveData<Event<Boolean>> get() = _isValidMeetingTime

    init {
        initializeMeetingTime()
    }

    private fun initializeMeetingTime() {
        val now = LocalTime.now()
        _meetingHour.value = now.hour
        _meetingMinute.value = now.minute
    }

    fun validMeetingTime(hour: Int, minute: Int) {
        // 이전에 선택한 Meeting Date와 함께 유효성 검증
        // 유효한 time이라면 liveData에 반영
        _isValidMeetingTime.emit(false)
    }
}
