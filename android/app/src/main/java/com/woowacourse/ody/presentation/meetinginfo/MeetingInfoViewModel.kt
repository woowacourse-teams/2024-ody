package com.woowacourse.ody.presentation.meetinginfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.presentation.address.AddressValidator
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit
import java.time.LocalTime

class MeetingInfoViewModel : ViewModel() {
    val isValidInfo: MediatorLiveData<Boolean> = MediatorLiveData()

    val meetingYear: MutableLiveData<Int> = MutableLiveData()
    val meetingMonth: MutableLiveData<Int> = MutableLiveData()
    val meetingDay: MutableLiveData<Int> = MutableLiveData()

    val meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: MutableLiveData<Int> = MutableLiveData()

    private val _isValidMeetingTime: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isValidMeetingTime: LiveData<Event<Boolean>> get() = _isValidMeetingTime

    val meetingName: MutableLiveData<String> = MutableLiveData()
    val meetingNameLength: LiveData<Int> = meetingName.map { it.length }

    private val _destinationGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val destinationGeoLocation: LiveData<GeoLocation> get() = _destinationGeoLocation

    private val _invalidDestinationEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidDestinationEvent: LiveData<Event<Unit>> get() = _invalidDestinationEvent

    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }

    private val _startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
    val startingPointGeoLocation: LiveData<GeoLocation> get() = _startingPointGeoLocation

    private val _invalidStartingPointEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidStartingPointEvent: LiveData<Event<Unit>> get() = _invalidStartingPointEvent

    init {
        initializeMeetingTime()
        initializeIsValidInfo()
    }

    private fun initializeMeetingTime() {
        val now = LocalTime.now()
        meetingHour.value = now.hour
        meetingMinute.value = now.minute
    }

    private fun initializeIsValidInfo() {
        isValidInfo.addSource(meetingName) {
            isValidInfo.value = it.isNotEmpty()
        }
        isValidInfo.addSource(_destinationGeoLocation) {
            val isValidDestinationEvent = AddressValidator.isValid(it.address)
            isValidInfo.value = isValidDestinationEvent
            if (!isValidDestinationEvent) {
                _invalidDestinationEvent.emit(Unit)
            }
        }
        isValidInfo.addSource(nickname) {
            isValidInfo.value = it.isNotEmpty()
        }
        isValidInfo.addSource(_startingPointGeoLocation) {
            val isValidStartingPoint = AddressValidator.isValid(it.address)
            isValidInfo.value = isValidStartingPoint
            if (!isValidStartingPoint) {
                _invalidStartingPointEvent.emit(Unit)
            }
        }
    }

    fun setDestinationGeoLocation(geoLocation: GeoLocation) {
        _destinationGeoLocation.value = geoLocation
    }

    fun emptyNickname() {
        nickname.value = ""
    }

    fun setStartingPointGeoLocation(geoLocation: GeoLocation) {
        _startingPointGeoLocation.value = geoLocation
    }

    fun validMeetingTime() {
        // 이전에 선택한 Meeting Date와 함께 유효성 검증
        _isValidMeetingTime.emit(false)
    }

    fun emptyMeetingName() {
        meetingName.value = ""
    }

    fun onNextInfo() {
        isValidInfo.value = false
    }

    companion object {
        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
