package com.woowacourse.ody.presentation.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingCreationViewModel : ViewModel() {
    val meetingInfoType: MutableLiveData<MeetingInfoType> = MutableLiveData()
    val isValidInfo: MediatorLiveData<Boolean> = MediatorLiveData(false)

    val meetingName: MutableLiveData<String> = MutableLiveData()
    val meetingNameLength: LiveData<Int> = meetingName.map { it.length }

    val meetingDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    private val _invalidMeetingDateEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidMeetingDateEvent: SingleLiveData<Unit> get() = _invalidMeetingDateEvent

    val meetingHour: MutableLiveData<Int> = MutableLiveData()
    val meetingMinute: MutableLiveData<Int> = MutableLiveData()

    private val _invalidMeetingTimeEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidMeetingTimeEvent: SingleLiveData<Unit> get() = _invalidMeetingTimeEvent

    val destinationGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidDestinationEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidDestinationEvent: SingleLiveData<Unit> get() = _invalidDestinationEvent

    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }

    val startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidStartingPointEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidStartingPointEvent: SingleLiveData<Unit> get() = _invalidStartingPointEvent

    private val _nextPageEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val nextPageEvent: SingleLiveData<Unit> = _nextPageEvent

    init {
        initializeIsValidInfo()
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

    fun initializeMeetingTime() {
        val isInitializedValue = meetingHour.value != null || meetingMinute.value != null
        if (isInitializedValue) return

        val now = LocalTime.now()
        meetingHour.value = now.hour
        meetingMinute.value = now.minute
    }

    fun emptyMeetingName() {
        meetingName.value = ""
    }

    fun emptyNickname() {
        nickname.value = ""
    }

    private fun isValidMeetingName(): Boolean {
        val meetingName = meetingName.value ?: return false
        return meetingName.isNotEmpty()
    }

    private fun isValidMeetingDateTime(): Boolean {
        val date = meetingDate.value ?: return false
        val hour = meetingHour.value ?: return false
        val minute = meetingMinute.value ?: return false
        val dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute))
        return LocalDateTime.now().isBefore(dateTime)
    }

    private fun isValidDestination(): Boolean {
        val destinationGeoLocation = destinationGeoLocation.value ?: return false
        return AddressValidator.isValid(destinationGeoLocation.address).also {
            if (!it) _invalidDestinationEvent.setValue(Unit)
        }
    }

    private fun isValidNickName(): Boolean {
        val nickName = nickname.value ?: return false
        return nickName.isNotEmpty()
    }

    private fun isValidStartingPoint(): Boolean {
        val startingPointGeoLocation = startingPointGeoLocation.value ?: return false
        return AddressValidator.isValid(startingPointGeoLocation.address).also {
            if (!it) _invalidStartingPointEvent.setValue(Unit)
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

    fun checkMeetingDateValidity(meetingDate: LocalDate) {
        val now = LocalDate.now()
        if (now.isAfter(meetingDate)) {
            _invalidMeetingDateEvent.setValue(Unit)
            return
        }
        this.meetingDate.value = meetingDate
    }

    fun moveOnNextPage() {
        checkInfoValidity()
        if (isValidInfo.value == true) {
            _nextPageEvent.setValue(Unit)
            return
        }
        if (meetingInfoType.value == MeetingInfoType.TIME) {
            _invalidMeetingTimeEvent.setValue(Unit)
        }
    }

    companion object {
        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
