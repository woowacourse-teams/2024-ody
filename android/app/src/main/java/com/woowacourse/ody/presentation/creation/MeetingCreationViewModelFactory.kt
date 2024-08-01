package com.woowacourse.ody.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class MeetingCreationViewModelFactory(
    private val meetingRepository: MeetingRepository,
    private val joinRepository: JoinRepository,
    private val inviteCodeRepository: InviteCodeRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingCreationViewModel::class.java)) {
            MeetingCreationViewModel(
                meetingRepository,
                joinRepository,
                inviteCodeRepository,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
