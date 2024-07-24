package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressValidator
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit
import java.time.LocalTime

class MeetingInfoViewModel : ViewModel() {
    val meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: MutableLiveData<Int> = MutableLiveData()

    private val _isValidMeetingTime: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isValidMeetingTime: LiveData<Event<Boolean>> get() = _isValidMeetingTime

    val meetingName: MutableLiveData<String> = MutableLiveData()
    val meetingNameLength: LiveData<Int> = meetingName.map { it.length }
    val hasMeetingName: LiveData<Boolean> = meetingName.map { it.isNotEmpty() }

    private val _destinationGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val destinationGeoLocation: LiveData<GeoLocation> get() = _destinationGeoLocation

    val isValidDestination: LiveData<Boolean> = _destinationGeoLocation.map { AddressValidator.isValid(it.address) }

    init {
        initializeMeetingTime()
    }

    private fun initializeMeetingTime() {
        val now = LocalTime.now()
        meetingHour.value = now.hour
        meetingMinute.value = now.minute
    }

    fun setDestinationGeoLocation(geoLocation: GeoLocation) {
        _destinationGeoLocation.value = geoLocation
    }

    fun validMeetingTime() {
        // 이전에 선택한 Meeting Date와 함께 유효성 검증
        _isValidMeetingTime.emit(false)
    }

    fun emptyMeetingName() {
        meetingName.value = ""
    }

    companion object {
        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
    }
}
