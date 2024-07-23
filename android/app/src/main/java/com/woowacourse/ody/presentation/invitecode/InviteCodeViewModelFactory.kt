package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.MeetingRepository

class InviteCodeViewModelFactory(
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InviteCodeViewModel::class.java)) {
            return InviteCodeViewModel(meetingRepository) as T
        }
        throw IllegalArgumentException()
    }
}
