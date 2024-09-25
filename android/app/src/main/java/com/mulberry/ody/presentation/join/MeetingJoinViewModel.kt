package com.mulberry.ody.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.model.GeoLocation
import com.mulberry.ody.domain.model.MeetingJoinInfo
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
    ) : BaseViewModel(), MeetingJoinListener {
        val departureGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()
        val departureAddress: LiveData<String> = departureGeoLocation.map { it.address }

        private val _invalidDepartureEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
        val invalidDepartureEvent: SingleLiveData<Unit> get() = _invalidDepartureEvent
        val isValidDeparture: LiveData<Boolean> = departureGeoLocation.map { isValidDeparturePoint() }

        private val _navigateAction: MutableSingleLiveData<MeetingJoinNavigateAction> =
            MutableSingleLiveData()
        val navigateAction: SingleLiveData<MeetingJoinNavigateAction> get() = _navigateAction

        fun joinMeeting(inviteCode: String) {
            val departureAddress = departureGeoLocation.value?.address ?: return
            val departureLatitude = departureGeoLocation.value?.latitude ?: return
            val departureLongitude = departureGeoLocation.value?.longitude ?: return

            viewModelScope.launch {
                startLoading()
                joinRepository.postMates(
                    MeetingJoinInfo(
                        inviteCode,
                        departureAddress,
                        departureLatitude,
                        departureLongitude,
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
            val departureGeoLocation = departureGeoLocation.value ?: return false
            return AddressValidator.isValid(departureGeoLocation.address).also {
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
            val startReserveTimeMilliSeconds = meetingTimeMilliSeconds + START_RESERVE_MILLI_SECOND

            val nowMilliSeconds = LocalDateTime.now().toMilliSeconds(LOCAL_ZONE_ID) + 3 * MILLI_SECOND_OF_SECOND
            val initialTimeMilliSeconds =
                if (nowMilliSeconds > startReserveTimeMilliSeconds) nowMilliSeconds else startReserveTimeMilliSeconds

            matesEtaRepository.reserveEtaFetchingJob(meetingId, initialTimeMilliSeconds, endReserveTimeMilliSeconds, RESERVE_INTERVAL)
        }

        companion object {
            private const val TAG = "MeetingJoinViewModel"

            private const val LOCAL_ZONE_ID = "Asia/Seoul"
            private const val MILLI_SECOND_OF_SECOND = 1_000L
            private const val MILLI_SECOND_OF_MINUTE = MILLI_SECOND_OF_SECOND * 60
            private const val START_RESERVE_MILLI_SECOND = -30 * MILLI_SECOND_OF_MINUTE
            private const val END_RESERVE_MILLI_SECOND = 1 * MILLI_SECOND_OF_MINUTE
            private const val RESERVE_INTERVAL = 10 * MILLI_SECOND_OF_MINUTE
        }
    }

fun LocalDateTime.toMilliSeconds(zoneId: String): Long = atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
