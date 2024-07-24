package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.woowacourse.ody.domain.repository.MeetingRepository
import com.woowacourse.ody.util.Event
import com.woowacourse.ody.util.emit
import kotlinx.coroutines.launch

class InviteCodeViewModel(
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    val inviteCode: MutableLiveData<String> = MutableLiveData()
    val hasInviteCode: LiveData<Boolean> = inviteCode.map { it.isNotEmpty() }

    private val _isValidInviteCode: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isValidInviteCode: LiveData<Event<Boolean>> get() = _isValidInviteCode

    fun emptyInviteCode() {
        inviteCode.value = ""
    }

    fun checkInviteCode() {
        viewModelScope.launch {
            val inviteCode = inviteCode.value ?: return@launch
            meetingRepository.getInviteCodeValidity(inviteCode)
                .onSuccess {
                    _isValidInviteCode.emit(true)
                }.onFailure {
                    _isValidInviteCode.emit(false)
                }
        }
    }
}
