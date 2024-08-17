package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.apiresult.onFailure
import com.woowacourse.ody.domain.apiresult.onNetworkError
import com.woowacourse.ody.domain.apiresult.onSuccess
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import com.woowacourse.ody.presentation.common.MutableSingleLiveData
import com.woowacourse.ody.presentation.common.SingleLiveData
import com.woowacourse.ody.presentation.common.analytics.AnalyticsHelper
import com.woowacourse.ody.presentation.common.analytics.logNetworkErrorEvent
import kotlinx.coroutines.launch
import timber.log.Timber

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

    private val _networkErrorEvent: MutableSingleLiveData<Unit> = MutableSingleLiveData()
    val networkErrorEvent: SingleLiveData<Unit> get() = _networkErrorEvent

    private var lastFailedAction: (() -> Unit)? = null

    fun clearInviteCode() {
        inviteCode.value = ""
    }

    fun checkInviteCode() {
        viewModelScope.launch {
            val inviteCode = inviteCode.value ?: return@launch
            meetingRepository.fetchInviteCodeValidity(inviteCode)
                .onSuccess {
                    _navigateAction.setValue(InviteCodeNavigateAction.CodeNavigateToJoin)
                }.onFailure { code, errorMessage ->
                    _invalidInviteCodeEvent.setValue(Unit)
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    _networkErrorEvent.setValue(Unit)
                    lastFailedAction = { checkInviteCode() }
                }
        }
    }

    fun retryLastAction() {
        lastFailedAction?.invoke()
    }

    companion object {
        private const val TAG = "InviteCodeViewModel"
    }
}
