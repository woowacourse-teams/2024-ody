package com.woowacourse.ody.presentation.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class MeetingCreationViewModelFactory(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MeetingCreationViewModel::class.java)) {
            MeetingCreationViewModel(firebaseAnalytics, meetingRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
