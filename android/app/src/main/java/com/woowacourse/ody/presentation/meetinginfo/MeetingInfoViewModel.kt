package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalTime

class MeetingInfoViewModel : ViewModel() {
    private val _meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingHour: LiveData<Int> get() = _meetingHour

    private val _meetingMinute: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: LiveData<Int> get() = _meetingMinute

    init {
        initializeMeetingTime()
    }

    private fun initializeMeetingTime() {
        val now = LocalTime.now()
        _meetingHour.value = now.hour
        _meetingMinute.value = now.minute
    }
}
