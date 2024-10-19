package com.mulberry.ody.presentation.join

import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.apiresult.suspendOnSuccess
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.JoinRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.validator.AddressValidator
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.join.listener.MeetingJoinListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        private val joinRepository: JoinRepository,
        private val matesEtaRepository: MatesEtaRepository,
        private val addressRepository: AddressRepository,
        private val geoLocationHelper: LocationHelper,
    ) : BaseViewModel(), MeetingJoinListener {
        val departureAddress: MutableStateFlow<Address?> = MutableStateFlow(null)

        private val _invalidDepartureEvent: MutableSharedFlow<Unit> = MutableSharedFlow()
        val invalidDepartureEvent: SharedFlow<Unit> get() = _invalidDepartureEvent.asSharedFlow()

        val isValidDeparture: StateFlow<Boolean> =
            departureAddress.map {
                println("TEST")
                isValidDeparturePoint()
            }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS),
                    initialValue = false,
                )

        private val _navigateAction: MutableSharedFlow<MeetingJoinNavigateAction> = MutableSharedFlow(replay = 1)
        val navigateAction: SharedFlow<MeetingJoinNavigateAction> get() = _navigateAction.asSharedFlow()

        fun getDefaultLocation() {
            viewModelScope.launch {
                startLoading()

                geoLocationHelper.getCurrentCoordinate().onSuccess { location ->
                    val longitude = location.longitude.toString()
                    val latitude = location.latitude.toString()

                    addressRepository.fetchAddressesByCoordinate(longitude, latitude).onSuccess {
                        departureAddress.value =
                            Address(
                                detailAddress = it ?: "",
                                longitude = longitude,
                                latitude = latitude,
                            )
                    }.onFailure { code, errorMessage ->
                        handleError()
                        analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    }.onNetworkError {
                        handleNetworkError()
                    }
                }
                stopLoading()
            }
        }

        fun joinMeeting(inviteCode: String) {
            val meetingJoinInfo = createMeetingJoinInfo(inviteCode) ?: return

            viewModelScope.launch {
                startLoading()
                joinRepository.postMates(meetingJoinInfo)
                    .suspendOnSuccess {
                        matesEtaRepository.reserveEtaFetchingJob(it.meetingId, it.meetingDateTime)
                        _navigateAction.emit(MeetingJoinNavigateAction.JoinNavigateToRoom(it.meetingId))
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
            return MeetingJoinInfo(
                inviteCode = inviteCode,
                departureAddress = address,
            )
        }

        private suspend fun isValidDeparturePoint(): Boolean {
            println("isValidDeparturePoint")
            val departureAddress = departureAddress.value ?: return false
            println("isValidDeparturePoint 2 $departureAddress")
            return AddressValidator.isValid(departureAddress.detailAddress).also {
                println("isValidDeparturePoint 2 $it")
                if (!it) _invalidDepartureEvent.emit(Unit)
            }
        }

        override fun onClickMeetingJoin() {
            viewModelScope.launch {
                _navigateAction.emit(MeetingJoinNavigateAction.JoinNavigateToJoinComplete)
            }
        }

        companion object {
            private const val TAG = "MeetingJoinViewModel"
            private const val STATE_FLOW_SUBSCRIPTION_TIMEOUT_MILLIS = 5000L
        }
    }
