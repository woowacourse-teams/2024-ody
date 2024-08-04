package com.woowacourse.ody.presentation.join

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.domain.model.Meeting
import com.woowacourse.ody.domain.model.MeetingJoinInfo
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.validator.AddressValidator
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.creation.MeetingCreationInfoType
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

    val startingPointGeoLocation: MutableLiveData<GeoLocation> = MutableLiveData()

    private val _invalidStartingPointEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidStartingPointEvent: SingleLiveData<Unit> get() = _invalidStartingPointEvent

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
            addSource(startingPointGeoLocation) { checkInfoValidity() }
        }
    }

    fun clearNickname() {
        nickname.value = ""
    }

    fun joinMeeting() {
        val nickname = nickname.value ?: return
        val startingPointAddress = startingPointGeoLocation.value?.address ?: return
        val startingPointLatitude = startingPointGeoLocation.value?.latitude ?: return
        val startingPointLongitude = startingPointGeoLocation.value?.longitude ?: return

        viewModelScope.launch {
            joinRepository.postMates(
                MeetingJoinInfo(
                    inviteCode,
                    nickname,
                    startingPointAddress,
                    startingPointLatitude,
                    startingPointLongitude,
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
                MeetingJoinInfoType.DEPARTURE -> isValidStartingPoint()
            }
        isValidInfo.value = isValid
    }

    private fun isValidNickName(): Boolean {
        val nickName = nickname.value ?: return false
        return nickName.isNotEmpty()
    }

    private fun isValidStartingPoint(): Boolean {
        val startingPointGeoLocation = startingPointGeoLocation.value ?: return false
        return AddressValidator.isValid(startingPointGeoLocation.address).also {
            if (!it) _invalidStartingPointEvent.setValue(Unit)
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
