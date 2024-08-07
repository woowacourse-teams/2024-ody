package com.woowacourse.ody.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.join.listener.MeetingJoinListener
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.ZoneId

class MeetingJoinViewModel(
    private val inviteCode: String,
    private val joinRepository: JoinRepository,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModel(), MeetingJoinListener {
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
            }.onFailure {
                Timber.e(it.message)
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
        return nickName.isNotEmpty()
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
        RESERVE_OFFSET_MINUTE_RANGE.forEach { minute ->
            val reserveMilliSeconds = meetingTimeMilliSeconds - (MILLI_SECOND_OF_MINUTE * minute)
            matesEtaRepository.reserveEtaFetchingJob(meetingId, reserveMilliSeconds)
        }
    }

    companion object {
        const val NICK_NAME_MAX_LENGTH = 9
        private const val LOCAL_ZONE_ID = "Asia/Seoul"
        private val RESERVE_OFFSET_MINUTE_RANGE = (-1..30)
        private const val MILLI_SECOND_OF_MINUTE = 60_000
    }
}

fun LocalDateTime.toMilliSeconds(zoneId: String): Long = atZone(ZoneId.of(zoneId)).toInstant().toEpochMilli()