package com.ydo.ody.presentation.invitecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ydo.ody.domain.repository.ody.MeetingRepository
import com.ydo.ody.presentation.common.analytics.AnalyticsHelper

class InviteCodeViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InviteCodeViewModel::class.java)) {
            return InviteCodeViewModel(analyticsHelper, meetingRepository) as T
        }
        throw IllegalArgumentException()
    }
}
