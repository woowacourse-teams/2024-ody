package com.mulberry.ody.presentation.invitecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper

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
