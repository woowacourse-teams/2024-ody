package com.ydo.ody.presentation.join

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ydo.ody.domain.repository.ody.JoinRepository
import com.ydo.ody.domain.repository.ody.MatesEtaRepository
import com.ydo.ody.presentation.common.analytics.AnalyticsHelper

class MeetingJoinViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val inviteCode: String,
    private val joinRepository: JoinRepository,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingJoinViewModel::class.java)) {
            MeetingJoinViewModel(analyticsHelper, inviteCode, joinRepository, matesEtaRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
