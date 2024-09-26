package com.mulberry.ody.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.domain.model.MeetingJoinInfo
import com.mulberry.ody.domain.repository.location.AddressRepository
import com.mulberry.ody.domain.repository.ody.JoinRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.domain.validator.AddressValidator
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.join.listener.MeetingJoinListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class MeetingJoinViewModel
    @Inject
    constructor(
        private val analyticsHelper: AnalyticsHelper,
        private val joinRepository: JoinRepository,
        private val matesEtaRepository: MatesEtaRepository,
        private val addressRepository: AddressRepository,
    ) : BaseViewModel(), MeetingJoinListener {
        val departureAddress: MutableLiveData<Address> = MutableLiveData()

        private val _invalidDepartureEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
        val invalidDepartureEvent: SingleLiveData<Unit> get() = _invalidDepartureEvent
        val isValidDeparture: LiveData<Boolean> = departureAddress.map { isValidDeparturePoint() }

        private val _navigateAction: MutableSingleLiveData<MeetingJoinNavigateAction> =
            MutableSingleLiveData()
        val navigateAction: SingleLiveData<MeetingJoinNavigateAction> get() = _navigateAction

        fun getDefaultLocation(
            longitude: String,
            latitude: String,
        ) {
            viewModelScope.launch {
                startLoading()
                addressRepository.fetchAddressesByCoord(
                    longitude,
                    latitude,
                ).onSuccess {
                    departureAddress.value =
                        Address(
                            detailAddress = it ?: "",
                            longitude = longitude,
                            latitude = latitude,
                        )
                }.onFailure { code, errorMessage ->
                    handleError()
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                }
                stopLoading()
            }
        }

        fun joinMeeting(inviteCode: String) {
            val departureAddress = departureAddress.value ?: return

            viewModelScope.launch {
                startLoading()
                joinRepository.postMates(
                    MeetingJoinInfo(
                        inviteCode,
                        departureAddress.detailAddress,
                        departureAddress.latitude,
                        departureAddress.longitude,
                    ),
                ).onSuccess {
                    reserveEtaFetchingJobs(it.meetingId, it.meetingDateTime)
                    _navigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToRoom(it.meetingId))
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

        private fun isValidDeparturePoint(): Boolean {
            val departureAddress = departureAddress.value ?: return false
            return AddressValidator.isValid(departureAddress.detailAddress).also {
                if (!it) _invalidDepartureEvent.setValue(Unit)
            }
        }

        override fun onClickMeetingJoin() {
            _navigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToJoinComplete)
        }

        private fun reserveEtaFetchingJobs(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            val meetingTimeMilliSeconds = meetingDateTime.toMilliSeconds(LOCAL_ZONE_ID)
            val endReserveTimeMilliSeconds = meetingTimeMilliSeconds + END_RESERVE_MILLI_SECOND
            var currentReserveTimeMilliSeconds = meetingTimeMilliSeconds + START_RESERVE_MILLI_SECOND

            while (currentReserveTimeMilliSeconds <= endReserveTimeMilliSeconds) {
                matesEtaRepository.reserveEtaFetchingJob(meetingId, currentReserveTimeMilliSeconds)
                currentReserveTimeMilliSeconds += RESERVE_INTERVAL
            }
        }

        companion object {
            private const val TAG = "MeetingJoinViewModel"

            private const val LOCAL_ZONE_ID = "Asia/Seoul"
            private const val MILLI_SECOND_OF_MINUTE = 60_000
            private const val START_RESERVE_MILLI_SECOND = -30 * MILLI_SECOND_OF_MINUTE
            private const val END_RESERVE_MILLI_SECOND = 1 * MILLI_SECOND_OF_MINUTE
            private const val RESERVE_INTERVAL = 10_000
        }
    }

fun LocalDateTime.toMilliSeconds(zoneId: String): Long = atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
