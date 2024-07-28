package com.woowacourse.ody.presentation.meetingcreation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.Event
import com.woowacourse.ody.presentation.common.emit
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingCreationViewModel : ViewModel() {
    val meetingInfoType: MutableLiveData<MeetingInfoType> = MutableLiveData()
    val isValidInfo: MediatorLiveData<Boolean> = MediatorLiveData(false)

    val meetingYear: MutableLiveData<Int> = MutableLiveData()
    val meetingMonth: MutableLiveData<Int> = MutableLiveData()
    val meetingDay: MutableLiveData<Int> = MutableLiveData()

    val meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: MutableLiveData<Int> = MutableLiveData()

    private val _invalidMeetingTimeEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidMeetingTimeEvent: LiveData<Event<Unit>> get() = _invalidMeetingTimeEvent

    val meetingName: MutableLiveData<String> = MutableLiveData()
    val meetingNameLength: LiveData<Int> = meetingName.map { it.length }

    val destinationGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidDestinationEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidDestinationEvent: LiveData<Event<Unit>> get() = _invalidDestinationEvent

    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }

    val startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidStartingPointEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val invalidStartingPointEvent: LiveData<Event<Unit>> get() = _invalidStartingPointEvent

    private val _nextPageEvent: MutableLiveData<Event<Unit>> = MutableLiveData()
    val nextPageEvent: LiveData<Event<Unit>> = _nextPageEvent

    init {
        initializeIsValidInfo()
    }

    fun initializeMeetingTime() {
        if (meetingHour.value != null || meetingMinute.value != null) {
            return
        }
        val now = LocalTime.now()
        meetingHour.value = now.hour
        meetingMinute.value = now.minute
    }

    private fun initializeIsValidInfo() {
        with(isValidInfo) {
            addSource(meetingInfoType) { checkInfoValidity() }
            addSource(meetingName) { checkInfoValidity() }
            addSource(destinationGeoLocation) { checkInfoValidity() }
            addSource(nickname) { checkInfoValidity() }
            addSource(startingPointGeoLocation) { checkInfoValidity() }
            addSource(meetingHour) { isValidInfo.value = true }
            addSource(meetingMinute) { isValidInfo.value = true }
        }
    }

    fun emptyNickname() {
        nickname.value = ""
    }

    fun emptyMeetingName() {
        meetingName.value = ""
    }

    private fun isValidMeetingName(): Boolean {
        val meetingName = meetingName.value ?: return false
        return meetingName.isNotEmpty()
    }

    private fun isValidMeetingDateTime(): Boolean {
        val year = meetingYear.value ?: return false
        val month = meetingMonth.value ?: return false
        val day = meetingDay.value ?: return false
        val hour = meetingHour.value ?: return false
        val minute = meetingMinute.value ?: return false
        val dateTime = LocalDateTime.of(year, month, day, hour, minute)
        return LocalDateTime.now().isBefore(dateTime)
    }

    private fun isValidNickName(): Boolean {
        val nickName = nickname.value ?: return false
        return nickName.isNotEmpty()
    }

    private fun isValidDestination(): Boolean {
        val destinationGeoLocation = destinationGeoLocation.value ?: return false
        return AddressValidator.isValid(destinationGeoLocation.address).also {
            if (!it) _invalidDestinationEvent.emit(Unit)
        }
    }

    private fun isValidStartingPoint(): Boolean {
        val startingPointGeoLocation = startingPointGeoLocation.value ?: return false
        return AddressValidator.isValid(startingPointGeoLocation.address).also {
            if (!it) _invalidStartingPointEvent.emit(Unit)
        }
    }

    private fun checkInfoValidity() {
        val meetingInfoType = meetingInfoType.value ?: return
        val isValid =
            when (meetingInfoType) {
                MeetingInfoType.NAME -> isValidMeetingName()
                MeetingInfoType.DATE -> true
                MeetingInfoType.TIME -> isValidMeetingDateTime()
                MeetingInfoType.DESTINATION -> isValidDestination()
                MeetingInfoType.NICKNAME -> isValidNickName()
                MeetingInfoType.STARTING_POINT -> isValidStartingPoint()
            }
        isValidInfo.value = isValid
    }

    fun moveOnNextPage() {
        checkInfoValidity()
        if (isValidInfo.value == true) {
            _nextPageEvent.emit(Unit)
            return
        }
        if (meetingInfoType.value == MeetingInfoType.TIME) {
            _invalidMeetingTimeEvent.emit(Unit)
        }
    }

    companion object {
        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
