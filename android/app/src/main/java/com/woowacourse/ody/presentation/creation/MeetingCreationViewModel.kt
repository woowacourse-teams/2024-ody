package com.woowacourse.ody.presentation.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingCreationInfo
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.creation.listener.MeetingCreationListener
import com.woowacourse.ody.presentation.join.MeetingJoinNavigateAction
import com.woowacourse.ody.presentation.join.listener.MeetingJoinListener
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class MeetingCreationViewModel(
    private val meetingRepository: MeetingRepository,
    private val joinRepository: JoinRepository,
    private val inviteCodeRepository: InviteCodeRepository,
) : ViewModel(), MeetingCreationListener, MeetingJoinListener {
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

    private val _createMeetingResponse: MutableLiveData<Meeting?> = MutableLiveData(null)
    val createMeetingResponse: LiveData<Meeting?> get() = _createMeetingResponse

    private val _navigateAction: MutableSingleLiveData<MeetingCreationNavigateAction> =
        MutableSingleLiveData()
    val navigateAction: SingleLiveData<MeetingCreationNavigateAction> get() = _navigateAction

    private val _joinNavigateAction: MutableSingleLiveData<MeetingJoinNavigateAction> =
        MutableSingleLiveData()
    val joinNavigateAction: SingleLiveData<MeetingJoinNavigateAction> get() = _joinNavigateAction

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

    fun createMeeting() {
        val name = meetingName.value ?: return
        val date = meetingDate.value ?: return
        val destinationAddress = destinationGeoLocation.value?.address ?: return
        val destinationLatitude = destinationGeoLocation.value?.latitude ?: return
        val destinationLongitude = destinationGeoLocation.value?.longitude ?: return
        val startingPointAddress = startingPointGeoLocation.value?.address ?: return
        val startingPointLatitude = startingPointGeoLocation.value?.latitude ?: return
        val startingPointLongitude = startingPointGeoLocation.value?.longitude ?: return

        viewModelScope.launch {
            meetingRepository.postMeeting(
                MeetingCreationInfo(
                    name,
                    date.toString(),
                    LocalTime.of(meetingHour.value ?: 1, meetingMinute.value ?: 0).toString(),
                    destinationAddress,
                    destinationLatitude,
                    destinationLongitude,
                    nickname.value.toString(),
                    startingPointAddress,
                    startingPointLatitude,
                    startingPointLongitude,
                ),
            ).onSuccess {
                inviteCodeRepository.postInviteCode(it.inviteCode)
                _createMeetingResponse.value = it
            }.onFailure {
                Timber.e(it.message)
            }
        }
    }

    fun joinMeeting(inviteCode: String) {
        val nickname = nickname.value ?: return
        val startingPointAddress = startingPointGeoLocation.value?.address ?: return
        val startingPointLatitude = startingPointGeoLocation.value?.latitude ?: return
        val startingPointLongitude = startingPointGeoLocation.value?.longitude ?: return

        viewModelScope.launch {
            joinRepository.postMates(
                MeetingJoinInfo(
                    inviteCode,
                    nickname,
                    startingPointAddress,
                    startingPointLatitude,
                    startingPointLongitude,
                ),
            ).onSuccess {
                inviteCodeRepository.postInviteCode(it.inviteCode)
                _createMeetingResponse.value = it
            }.onFailure {
                Timber.e(it.message)
            }
        }
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

    fun clearMeetingName() {
        meetingName.value = ""
    }

    fun clearNickname() {
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

    fun updateMeetingDate(meetingDate: LocalDate) {
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

    fun navigateToRoom() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToRoom)
    }

    fun navigateToIntro() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToIntro)
    }

    override fun onClickCreationMeeting() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToCreationComplete)
    }

    override fun onClickMJoinMeeting() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToJoinComplete)
    }

    fun navigateJoinToRoom() {
        _joinNavigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToRoom)
    }

    override fun onClickMeetingJoin() {
        _joinNavigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToJoinComplete)
    }

    companion object {
        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
