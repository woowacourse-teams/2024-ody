package com.mulberry.ody.presentation.creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.validator.AddressValidator
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.creation.listener.MeetingCreationListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class MeetingCreationViewModel
@Inject
constructor(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel(), MeetingCreationListener {
    val meetingCreationInfoType: MutableLiveData<MeetingCreationInfoType> = MutableLiveData()
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

    val destinationAddress: MutableLiveData<Address> = MutableLiveData()

    private val _invalidDestinationEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidDestinationEvent: SingleLiveData<Unit> get() = _invalidDestinationEvent

    private val _nextPageEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val nextPageEvent: SingleLiveData<Unit> = _nextPageEvent

    private val _navigateAction: MutableSingleLiveData<MeetingCreationNavigateAction> =
        MutableSingleLiveData()
    val navigateAction: SingleLiveData<MeetingCreationNavigateAction> get() = _navigateAction

    private val _inviteCode: MutableLiveData<String> = MutableLiveData()
    val inviteCode: LiveData<String> get() = _inviteCode

    init {
        initializeIsValidInfo()
    }

    private fun initializeIsValidInfo() {
        with(isValidInfo) {
            addSource(meetingCreationInfoType) { checkInfoValidity() }
            addSource(meetingName) { checkInfoValidity() }
            addSource(destinationAddress) { checkInfoValidity() }
            addSource(meetingHour) { isValidInfo.value = true }
            addSource(meetingMinute) { isValidInfo.value = true }
        }
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
        val meetingCreationInfo = createMeetingCreationInfo() ?: return

        viewModelScope.launch {
            startLoading()
            meetingRepository.postMeeting(meetingCreationInfo)
                .onSuccess {
                    _inviteCode.value = it
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { createMeeting() }
                }
            stopLoading()
        }
    }

    private fun createMeetingCreationInfo(): MeetingCreationInfo? {
        val name = meetingName.value ?: return null
        val date = meetingDate.value ?: return null
        val hour = meetingHour.value ?: return null
        val minute = meetingMinute.value ?: return null
        val address = destinationAddress.value ?: return null

        return MeetingCreationInfo(
            name = name,
            dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute)),
            destinationAddress = address,
        )
    }

    fun clearMeetingName() {
        meetingName.value = ""
    }

    private fun checkInfoValidity() {
        val meetingCreationInfoType = meetingCreationInfoType.value ?: return
        val isValid =
            when (meetingCreationInfoType) {
                MeetingCreationInfoType.NAME -> isValidMeetingName()
                MeetingCreationInfoType.DATE -> true
                MeetingCreationInfoType.TIME -> isValidMeetingDateTime()
                MeetingCreationInfoType.DESTINATION -> isValidDestination()
            }
        isValidInfo.value = isValid
    }

    private fun isValidMeetingName(): Boolean {
        val meetingName = meetingName.value ?: return false
        return (meetingName.isNotEmpty() && meetingName.length <= MEETING_NAME_MAX_LENGTH)
    }

    private fun isValidMeetingDateTime(): Boolean {
        val date = meetingDate.value ?: return false
        val hour = meetingHour.value ?: return false
        val minute = meetingMinute.value ?: return false
        val dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute))
        return LocalDateTime.now().isBefore(dateTime)
    }

    private fun isValidDestination(): Boolean {
        val destinationAddress = destinationAddress.value ?: return false
        return AddressValidator.isValid(destinationAddress.detailAddress).also {
            if (!it) _invalidDestinationEvent.setValue(Unit)
        }
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
        if (meetingCreationInfoType.value == MeetingCreationInfoType.TIME) {
            _invalidMeetingTimeEvent.setValue(Unit)
        }
    }

    fun navigateToIntro() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToMeetings)
    }

    override fun onClickCreationMeeting() {
        _navigateAction.setValue(MeetingCreationNavigateAction.NavigateToCreationComplete)
    }

    companion object {
        private const val TAG = "MeetingCreationViewModel"

        val MEETING_HOURS = (0..<24).toList()
        val MEETING_MINUTES = (0..<60).toList()
        const val MEETING_NAME_MAX_LENGTH = 15
    }
}
