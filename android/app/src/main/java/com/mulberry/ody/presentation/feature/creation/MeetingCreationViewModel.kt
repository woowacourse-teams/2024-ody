package com.mulberry.ody.presentation.feature.creation

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.onUnexpected
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
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
    ) : BaseViewModel() {
        private val _meetingCreationUiModel = MutableStateFlow(MeetingCreationUiModel())
        val meetingCreationUiModel: StateFlow<MeetingCreationUiModel> get() = _meetingCreationUiModel.asStateFlow()

        private val _isCreationValid = MutableStateFlow(false)
        val isCreationValid: StateFlow<Boolean> get() = _isCreationValid.asStateFlow()

        private val _invalidDestinationEvent = MutableSharedFlow<Unit>()
        val invalidDestinationEvent: SharedFlow<Unit> = _invalidDestinationEvent.asSharedFlow()

        private val _navigateAction = MutableSharedFlow<MeetingCreationNavigateAction>()
        val navigateAction: SharedFlow<MeetingCreationNavigateAction> = _navigateAction.asSharedFlow()

        private val _currentLocationError: MutableSharedFlow<Unit> = MutableSharedFlow()
        val currentLocationError: SharedFlow<Unit> get() = _currentLocationError.asSharedFlow()

        fun getCurrentLocation() {
            viewModelScope.launch {
                startLoading()
                locationHelper.getCurrentCoordinate()
                    .onSuccess { location ->
                        fetchAddressesByCoordinate(location)
                    }
                    .onUnexpected {
                        _currentLocationError.emit(Unit)
                    }
                stopLoading()
            }
        }

        private suspend fun fetchAddressesByCoordinate(location: Location) {
            val longitude = location.longitude.toString()
            val latitude = location.latitude.toString()

            addressRepository.fetchAddressesByCoordinate(longitude, latitude).onSuccess {
                val address =
                    Address(
                        detailAddress = it ?: "",
                        longitude = longitude,
                        latitude = latitude,
                    )
                updateMeetingDestination(address)
            }.onFailure { code, errorMessage ->
                handleError()
                analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
            }.onUnexpected {
                _currentLocationError.emit(Unit)
            }.onNetworkError {
                handleNetworkError()
            }
        }

        fun createMeeting() {
            viewModelScope.launch {
                val meetingCreationInfo = _meetingCreationUiModel.value.convertMeetingCreationInfo() ?: return@launch
                startLoading()
                meetingRepository.postMeeting(meetingCreationInfo)
                    .onSuccess {
                        _navigateAction.emit(MeetingCreationNavigateAction.NavigateToMeetingJoin(it))
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

        fun updateMeetingName(name: String) {
            viewModelScope.launch {
                val oldUiModel = _meetingCreationUiModel.value
                _meetingCreationUiModel.emit(oldUiModel.copy(name = name))
                val newUiModel = _meetingCreationUiModel.value
                _isCreationValid.emit(newUiModel.isValidName())
            }
        }

        fun updateMeetingDate(date: LocalDate) {
            viewModelScope.launch {
                val oldUiModel = _meetingCreationUiModel.value
                _meetingCreationUiModel.emit(oldUiModel.copy(date = date))
                val newUiModel = _meetingCreationUiModel.value
                _isCreationValid.emit(newUiModel.isValidDate())
            }
        }

        fun updateMeetingTime(time: LocalTime) {
            viewModelScope.launch {
                val oldUiModel = _meetingCreationUiModel.value
                _meetingCreationUiModel.emit(oldUiModel.copy(time = time))
                val newUiModel = _meetingCreationUiModel.value
                _isCreationValid.emit(newUiModel.isValidTime())
            }
        }

        fun updateMeetingDestination(destination: Address) {
            viewModelScope.launch {
                val oldUiModel = _meetingCreationUiModel.value
                _meetingCreationUiModel.emit(oldUiModel.copy(destination = destination))
                val newUiModel = _meetingCreationUiModel.value
                _isCreationValid.emit(newUiModel.isValidDestination())

                if (!newUiModel.isValidDestination()) {
                    _invalidDestinationEvent.emit(Unit)
                }
            }
        }

        companion object {
            private const val TAG = "MeetingCreationViewModel"
            const val MEETING_NAME_MAX_LENGTH = 15
        }
    }
