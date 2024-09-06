package com.mulberry.ody.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper

class MeetingCreationViewModelFactory(
    private val analyticsHelper: AnalyticsHelper,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingCreationViewModel::class.java)) {
            MeetingCreationViewModel(analyticsHelper, meetingRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
