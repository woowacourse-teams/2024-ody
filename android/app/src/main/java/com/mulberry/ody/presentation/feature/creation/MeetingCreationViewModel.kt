package com.mulberry.ody.presentation.feature.creation

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.onUnexpected
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingCreationInfo
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
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
        private val _invalidMeetingTimeEvent = MutableSharedFlow<Unit>()
        val invalidMeetingTimeEvent: SharedFlow<Unit> = _invalidMeetingTimeEvent.asSharedFlow()

        val destinationAddress = MutableStateFlow<Address?>(null)

        private val _invalidDestinationEvent = MutableSharedFlow<Unit>()
        val invalidDestinationEvent: SharedFlow<Unit> = _invalidDestinationEvent.asSharedFlow()

        private val _navigateAction = MutableSharedFlow<MeetingCreationNavigateAction>()
        val navigateAction: SharedFlow<MeetingCreationNavigateAction> = _navigateAction.asSharedFlow()

        private val _currentLocationError: MutableSharedFlow<Unit> = MutableSharedFlow()
        val currentLocationError: SharedFlow<Unit> get() = _currentLocationError.asSharedFlow()

        fun getDefaultLocation() {
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
                destinationAddress.value =
                    Address(
                        detailAddress = it ?: "",
                        longitude = longitude,
                        latitude = latitude,
                    )
            }.onFailure { code, errorMessage ->
                handleError()
                analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
            }.onUnexpected {
                _currentLocationError.emit(Unit)
            }.onNetworkError {
                handleNetworkError()
            }
        }

        fun createMeeting(meetingCreationInfo: MeetingCreationInfo) {
            viewModelScope.launch {
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
                        lastFailedAction = { createMeeting(meetingCreationInfo) }
                    }
                stopLoading()
            }
        }

        companion object {
            private const val TAG = "MeetingCreationViewModel"
            const val MEETING_NAME_MAX_LENGTH = 15
        }
    }
