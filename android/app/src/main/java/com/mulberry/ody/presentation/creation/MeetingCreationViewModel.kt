package com.mulberry.ody.presentation.creation

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.validator.AddressValidator
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.creation.listener.MeetingCreationListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
        val meetingCreationInfoType = MutableStateFlow<MeetingCreationInfoType?>(null)

        val isValidInfo = MutableStateFlow(false)

        val meetingName: MutableStateFlow<String> = MutableStateFlow("")
        val meetingNameLength: StateFlow<Int> =
            meetingName.map { it.length }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

        val meetingDate = MutableStateFlow(LocalDate.now())

        private val _invalidMeetingDateEvent = MutableSharedFlow<Unit>()
        val invalidMeetingDateEvent: SharedFlow<Unit> = _invalidMeetingDateEvent

        val meetingHour = MutableStateFlow<Int?>(null)
        val meetingMinute = MutableStateFlow<Int?>(null)

        private val _invalidMeetingTimeEvent = MutableSharedFlow<Unit>()
        val invalidMeetingTimeEvent: SharedFlow<Unit> = _invalidMeetingTimeEvent

        val destinationAddress = MutableStateFlow<Address?>(null)

        private val _invalidDestinationEvent = MutableSharedFlow<Unit>()
        val invalidDestinationEvent: SharedFlow<Unit> = _invalidDestinationEvent

        private val _nextPageEvent = MutableSharedFlow<Unit>()
        val nextPageEvent: SharedFlow<Unit> = _nextPageEvent

        private val _navigateAction = MutableSharedFlow<MeetingCreationNavigateAction>()
        val navigateAction: SharedFlow<MeetingCreationNavigateAction> = _navigateAction

        private val _inviteCode = MutableStateFlow<String?>(null)
        val inviteCode: StateFlow<String?> = _inviteCode

        init {
            initializeIsValidInfo()
        }

        private fun initializeIsValidInfo() {
            viewModelScope.launch {
                combine(
                    meetingCreationInfoType,
                    meetingName,
                    destinationAddress,
                    meetingHour,
                    meetingMinute,
                ) { _, _, _, _, _ ->
                    checkInfoValidity()
                }.collect { isValid ->
                    isValidInfo.value = isValid
                }
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
            val name = meetingName.value.takeIf { it.isNotEmpty() } ?: return null
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

        private fun checkInfoValidity(): Boolean {
            val meetingCreationInfoType = meetingCreationInfoType.value ?: return false

            return when (meetingCreationInfoType) {
                MeetingCreationInfoType.NAME -> isValidMeetingName()
                MeetingCreationInfoType.DATE -> true
                MeetingCreationInfoType.TIME -> isValidMeetingDateTime()
                MeetingCreationInfoType.DESTINATION -> isValidDestination()
            }
        }

        private fun isValidMeetingName(): Boolean {
            val meetingName = meetingName.value
            return meetingName.isNotEmpty() && meetingName.length <= MEETING_NAME_MAX_LENGTH
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
                viewModelScope.launch {
                    if (!it) _invalidDestinationEvent.emit(Unit)
                }
            }
        }

        fun updateMeetingDate(meetingDate: LocalDate) {
            val now = LocalDate.now()
            if (now.isAfter(meetingDate)) {
                viewModelScope.launch { _invalidMeetingDateEvent.emit(Unit) }
                return
            }
            this.meetingDate.value = meetingDate
        }

        fun moveOnNextPage() {
            viewModelScope.launch {
                if (isValidInfo.value) {
                    _nextPageEvent.emit(Unit)
                } else if (meetingCreationInfoType.value == MeetingCreationInfoType.TIME) {
                    _invalidMeetingTimeEvent.emit(Unit)
                }
            }
        }

        fun navigateToIntro() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingCreationNavigateAction.NavigateToMeetings)
            }
        }

        override fun onClickCreationMeeting() {
            val inviteCode = _inviteCode.value ?: return
            viewModelScope.launch {
                _navigateAction.emit(
                    MeetingCreationNavigateAction.NavigateToMeetingJoin(inviteCode),
                )
            }
        }

        companion object {
            private const val TAG = "MeetingCreationViewModel"

            val MEETING_HOURS = (0..<24).toList()
            val MEETING_MINUTES = (0..<60).toList()
            const val MEETING_NAME_MAX_LENGTH = 15
        }
    }
