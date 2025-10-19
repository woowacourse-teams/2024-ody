package com.mulberry.ody.presentation.feature.join

import android.location.Location
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.onUnexpected
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.usecase.GetAddressNameByCoordinateUseCase
import com.mulberry.ody.domain.usecase.JoinMeetingUseCase
import com.mulberry.ody.domain.usecase.OpenEtaDashboardUseCase
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.feature.join.model.MeetingJoinNavigateAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MeetingJoinViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val joinMeetingUseCase: JoinMeetingUseCase,
        private val getAddressNameByCoordinateUseCase: GetAddressNameByCoordinateUseCase,
        private val openEtaDashboardUseCase: OpenEtaDashboardUseCase,
        private val locationHelper: LocationHelper,
    ) : BaseViewModel() {
        private val _departureAddress: MutableStateFlow<Address?> = MutableStateFlow(null)
        val departureAddress: StateFlow<Address?> get() = _departureAddress.asStateFlow()

        private val _invalidDepartureEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val invalidDepartureEvent: SharedFlow<Unit> get() = _invalidDepartureEvent.asSharedFlow()

        val isJoinValid: StateFlow<Boolean> =
            departureAddress.map { it?.isValid() ?: false }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                    initialValue = false,
                )

        private val _navigateAction: MutableSharedFlow<MeetingJoinNavigateAction> = MutableSharedFlow(replay = 1)
        val navigateAction: SharedFlow<MeetingJoinNavigateAction> get() = _navigateAction.asSharedFlow()

        private val _currentLocationError: MutableSharedFlow<Unit> = MutableSharedFlow()
        val currentLocationError: SharedFlow<Unit> get() = _currentLocationError.asSharedFlow()

        fun updateMeetingDeparture(departure: Address) {
            viewModelScope.launch {
                if (departure.isValid()) {
                    _departureAddress.emit(departure)
                } else {
                    _invalidDepartureEvent.emit(Unit)
                }
            }
        }

        fun getCurrentLocation() {
            viewModelScope.launch {
                startLoading()
                locationHelper.getCurrentCoordinate()
                    .onSuccess { location ->
                        fetchAddressNameByCoordinate(location)
                    }
                    .onUnexpected {
                        _currentLocationError.emit(Unit)
                    }
                stopLoading()
            }
        }

        private suspend fun fetchAddressNameByCoordinate(location: Location) {
            val longitude = location.longitude.toString()
            val latitude = location.latitude.toString()

            getAddressNameByCoordinateUseCase(longitude, latitude).onSuccess {
                val address =
                    Address(
                        detailAddress = it ?: "",
                        longitude = longitude,
                        latitude = latitude,
                    )
                updateMeetingDeparture(address)
            }.onFailure { code, errorMessage ->
                handleError()
                analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
            }.onUnexpected {
                _currentLocationError.emit(Unit)
            }.onNetworkError {
                handleNetworkError()
            }
        }

        fun joinMeeting(inviteCode: String) {
            val meetingJoinInfo = createMeetingJoinInfo(inviteCode) ?: return

            viewModelScope.launch {
                startLoading()
                joinMeetingUseCase(meetingJoinInfo)
                    .onSuccess { etaOpenInfo ->
                        openEtaDashboardUseCase(etaOpenInfo)
                        _navigateAction.emit(MeetingJoinNavigateAction.JoinNavigateToRoom(etaOpenInfo.meetingId))
                        _navigateAction.emit(MeetingJoinNavigateAction.JoinNavigateToJoinComplete)
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                        Timber.e("$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                        lastFailedAction = { joinMeeting(inviteCode) }
                    }
                stopLoading()
            }
        }

        private fun createMeetingJoinInfo(inviteCode: String): MeetingJoinInfo? {
            val address = departureAddress.value ?: return null
            return MeetingJoinInfo(inviteCode = inviteCode, departureAddress = address)
        }

        companion object {
            private const val TAG = "MeetingJoinViewModel"
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
        }
    }
