package com.mulberry.ody.presentation.invitecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.mulberry.ody.domain.apiresult.onFailure
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.onSuccess
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.BaseViewModel
import com.mulberry.ody.presentation.common.MutableSingleLiveData
import com.mulberry.ody.presentation.common.SingleLiveData
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import kotlinx.coroutines.launch
import timber.log.Timber

class InviteCodeViewModel(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : BaseViewModel() {
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
            startLoading()
            meetingRepository.fetchInviteCodeValidity(inviteCode)
                .onSuccess {
                    _navigateAction.setValue(InviteCodeNavigateAction.CodeNavigateToJoin)
                }.onFailure { code, errorMessage ->
                    _invalidInviteCodeEvent.setValue(Unit)
                    analyticsHelper.logNetworkErrorEvent(TAG, "$code $errorMessage")
                    Timber.e("$code $errorMessage")
                }.onNetworkError {
                    handleNetworkError()
                    lastFailedAction = { checkInviteCode() }
                }
            stopLoading()
        }
    }

    companion object {
        private const val TAG = "InviteCodeViewModel"
    }
}
