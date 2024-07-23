package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.repository.MeetingRepository

class InviteCodeViewModel(
    private val meetingRepository: MeetingRepository,
) : ViewModel() {
    val inviteCode: MutableLiveData<String> = MutableLiveData()

    val hasInviteCode: LiveData<Boolean> = inviteCode.map { it.isNotEmpty() }

    fun emptyInviteCode() {
        inviteCode.value = ""
    }
}
