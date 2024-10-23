package com.mulberry.ody.presentation.creation

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.apiresult.suspendOnUnexpected
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.domain.validator.AddressValidator
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.creation.listener.MeetingCreationListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
        private val addressRepository: AddressRepository,
        private val locationHelper: LocationHelper,
    ) : BaseViewModel(), MeetingCreationListener {
        val meetingCreationInfoType: MutableStateFlow<MeetingCreationInfoType?> = MutableStateFlow(null)

        val isValidInfo: MutableStateFlow<Boolean> = MutableStateFlow(false)

        val meetingName: MutableStateFlow<String> = MutableStateFlow("")
        val meetingNameLength: StateFlow<Int> =
            meetingName.map { it.length }
                .stateIn(
                    viewModelScope,
                    SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                    0,
                )

        val meetingDate = MutableStateFlow(LocalDate.now())

        private val _invalidMeetingDateEvent = MutableSharedFlow<Unit>()
        val invalidMeetingDateEvent: SharedFlow<Unit> = _invalidMeetingDateEvent.asSharedFlow()

        val meetingHour = MutableStateFlow(MEETING_HOUR_DEFAULT_VALUE)
        val meetingMinute = MutableStateFlow(MEETING_MINUTE_DEFAULT_VALUE)

        private val _invalidMeetingTimeEvent = MutableSharedFlow<Unit>()
        val invalidMeetingTimeEvent: SharedFlow<Unit> = _invalidMeetingTimeEvent.asSharedFlow()

        val destinationAddress = MutableStateFlow<Address?>(null)

        private val _invalidDestinationEvent = MutableSharedFlow<Unit>()
        val invalidDestinationEvent: SharedFlow<Unit> = _invalidDestinationEvent.asSharedFlow()

        private val _nextPageEvent = MutableSharedFlow<Unit>()
        val nextPageEvent: SharedFlow<Unit> = _nextPageEvent.asSharedFlow()

        private val _navigateAction = MutableSharedFlow<MeetingCreationNavigateAction>()
        val navigateAction: SharedFlow<MeetingCreationNavigateAction> = _navigateAction.asSharedFlow()

        private val _inviteCode = MutableStateFlow("")
        val inviteCode: StateFlow<String> = _inviteCode.asStateFlow()

        private val _defaultLocationError: MutableSharedFlow<Unit> = MutableSharedFlow()
        val defaultLocationError: SharedFlow<Unit> get() = _defaultLocationError.asSharedFlow()

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
            if (meetingHour.value != MEETING_HOUR_DEFAULT_VALUE || meetingMinute.value != MEETING_MINUTE_DEFAULT_VALUE) return
            val now = LocalTime.now()
            meetingHour.value = now.hour
            meetingMinute.value = now.minute
        }

        fun getDefaultLocation() {
            viewModelScope.launch {
                startLoading()
                locationHelper.getCurrentCoordinate()
                    .suspendOnSuccess { location ->
                        fetchAddressesByCoordinate(location)
                    }
                    .suspendOnUnexpected {
                        _defaultLocationError.emit(Unit)
                    }
                stopLoading()
            }
        }

        private suspend fun fetchAddressesByCoordinate(location: Location) {
            val longitude = location.longitude.toString()
            val latitude = location.latitude.toString()

            addressRepository.fetchAddressesByCoordinate(longitude, latitude).onSuccess {
                destinationAddress.value =
                    Address(
                        detailAddress = it ?: "",
                        longitude = longitude,
                        latitude = latitude,
                    )
            }.onFailure { code, errorMessage ->
                handleError()
                analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
            }.suspendOnUnexpected {
                _defaultLocationError.emit(Unit)
            }.onNetworkError {
                handleNetworkError()
            }
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
            val name = meetingName.value.ifBlank { return null }
            val date = meetingDate.value ?: return null

            val hour = meetingHour.value
            val minute = meetingMinute.value

            if (meetingHour.value == -1) return null
            if (meetingMinute.value == -1) return null

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
            return meetingName.isNotBlank() && meetingName.length <= MEETING_NAME_MAX_LENGTH
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
            val inviteCode = _inviteCode.value
            if (inviteCode.isBlank()) return

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
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
            private const val MEETING_HOUR_DEFAULT_VALUE = -1
            private const val MEETING_MINUTE_DEFAULT_VALUE = -1
        }
    }
