package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import kotlinx.coroutines.launch

class InviteCodeViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    val inviteCode: MutableLiveData<String> = MutableLiveData()
    val hasInviteCode: LiveData<Boolean> = inviteCode.map { it.isNotEmpty() }

    private val _invalidInviteCodeEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val invalidInviteCodeEvent: SingleLiveData<Unit> get() = _invalidInviteCodeEvent

    private val _navigateAction: MutableSingleLiveData<InviteCodeNavigateAction> = MutableSingleLiveData()
    val navigateAction: SingleLiveData<InviteCodeNavigateAction> get() = _navigateAction

    fun clearInviteCode() {
        inviteCode.value = ""
    }

    fun checkInviteCode() {
        viewModelScope.launch {
            val inviteCode = inviteCode.value ?: return@launch
            meetingRepository.fetchInviteCodeValidity(inviteCode)
                .onSuccess {
                    _navigateAction.setValue(InviteCodeNavigateAction.CodeNavigateToJoin)
                }.onFailure {
                    analyticsHelper.logNetworkErrorEvent(TAG, it.message)
                    _invalidInviteCodeEvent.setValue(Unit)
                }
        }
    }

    companion object {
        private const val TAG = "InviteCodeViewModel"
    }
}
