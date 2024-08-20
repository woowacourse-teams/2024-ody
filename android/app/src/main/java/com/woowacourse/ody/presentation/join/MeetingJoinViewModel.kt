package com.woowacourse.ody.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.BaseViewModel
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import com.woowacourse.ody.presentation.join.listener.MeetingJoinListener
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId

class MeetingJoinViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val inviteCode: String,
    private val joinRepository: JoinRepository,
    private val matesEtaRepository: MatesEtaRepository,
) : BaseViewModel(), MeetingJoinListener {
    val meetingJoinInfoType: MutableLiveData<MeetingJoinInfoType> = MutableLiveData()
    val isValidInfo: MediatorLiveData<Boolean> = MediatorLiveData(false)

    val nickname: MutableLiveData<String> = MutableLiveData()
    val nicknameLength: LiveData<Int> = nickname.map { it.length }

    val departureGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidDepartureEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidDepartureEvent: SingleLiveData<Unit> get() = _invalidDepartureEvent

    private val _navigateAction: MutableSingleLiveData<MeetingJoinNavigateAction> =
        MutableSingleLiveData()
    val navigateAction: SingleLiveData<MeetingJoinNavigateAction> get() = _navigateAction

    init {
        initializeIsValidInfo()
    }

    private fun initializeIsValidInfo() {
        with(isValidInfo) {
            addSource(meetingJoinInfoType) { checkInfoValidity() }
            addSource(nickname) { checkInfoValidity() }
            addSource(departureGeoLocation) { checkInfoValidity() }
        }
    }

    fun clearNickname() {
        nickname.value = ""
    }

    fun joinMeeting() {
        val nickname = nickname.value ?: return
        val departureAddress = departureGeoLocation.value?.address ?: return
        val departureLatitude = departureGeoLocation.value?.latitude ?: return
        val departureLongitude = departureGeoLocation.value?.longitude ?: return

        viewModelScope.launch {
            joinRepository.postMates(
                MeetingJoinInfo(
                    inviteCode,
                    nickname,
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
                lastFailedAction = { joinMeeting() }
            }
        }
    }

    private fun checkInfoValidity() {
        val meetingJoinInfoType = meetingJoinInfoType.value ?: return
        val isValid =
            when (meetingJoinInfoType) {
                MeetingJoinInfoType.NICKNAME -> isValidNickName()
                MeetingJoinInfoType.DEPARTURE -> isValidDeparturePoint()
            }
        isValidInfo.value = isValid
    }

    private fun isValidNickName(): Boolean {
        val nickName = nickname.value ?: return false
        return nickName.isNotEmpty() && nickName.length <= NICK_NAME_MAX_LENGTH
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
        var currentReserveTimeMilliSeconds = meetingTimeMilliSeconds + START_RESERVE_MILLI_SECOND

        while (currentReserveTimeMilliSeconds <= endReserveTimeMilliSeconds) {
            matesEtaRepository.reserveEtaFetchingJob(meetingId, currentReserveTimeMilliSeconds)
            currentReserveTimeMilliSeconds += RESERVE_INTERVAL
        }
    }

    companion object {
        private const val TAG = "MeetingJoinViewModel"

        const val NICK_NAME_MAX_LENGTH = 9
        private const val LOCAL_ZONE_ID = "Asia/Seoul"
        private const val MILLI_SECOND_OF_MINUTE = 60_000
        private const val START_RESERVE_MILLI_SECOND = -30 * MILLI_SECOND_OF_MINUTE
        private const val END_RESERVE_MILLI_SECOND = 1 * MILLI_SECOND_OF_MINUTE
        private const val RESERVE_INTERVAL = 10_000
    }
}

fun LocalDateTime.toMilliSeconds(zoneId: String): Long =
    atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()
