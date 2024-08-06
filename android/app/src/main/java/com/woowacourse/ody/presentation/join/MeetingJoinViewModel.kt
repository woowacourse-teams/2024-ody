package com.woowacourse.ody.presentation.join

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.join.listener.MeetingJoinListener
import kotlinx.coroutines.launch
import timber.log.Timber

class MeetingJoinViewModel(
    private val inviteCode: String,
    private val joinRepository: JoinRepository,
    private val inviteCodeRepository: InviteCodeRepository,
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
                inviteCodeRepository.postInviteCode(it.inviteCode)
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

    fun navigateJoinToRoom() {
        _navigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToRoom)
    }

    override fun onClickMeetingJoin() {
        _navigateAction.setValue(MeetingJoinNavigateAction.JoinNavigateToJoinComplete)
    }

    companion object {
        const val NICK_NAME_MAX_LENGTH = 9
    }
}
