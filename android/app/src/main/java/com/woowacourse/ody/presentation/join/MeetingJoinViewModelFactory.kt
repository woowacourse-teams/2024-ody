package com.woowacourse.ody.presentation.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.ody.InviteCodeRepository
import com.woowacourse.ody.domain.repository.ody.JoinRepository
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository

class MeetingJoinViewModelFactory(
    private val inviteCode: String,
    private val joinRepository: JoinRepository,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingJoinViewModel::class.java)) {
            MeetingJoinViewModel(inviteCode, joinRepository, matesEtaRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
