package com.woowacourse.ody.presentation.invitecode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.woowacourse.ody.domain.repository.ody.MeetingRepository

class InviteCodeViewModelFactory(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val meetingRepository: MeetingRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InviteCodeViewModel::class.java)) {
            return InviteCodeViewModel(firebaseAnalytics, meetingRepository) as T
        }
        throw IllegalArgumentException()
    }
}
