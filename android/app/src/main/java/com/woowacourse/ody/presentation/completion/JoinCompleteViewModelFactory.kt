package com.woowacourse.ody.presentation.completion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.data.local.db.OdyDatastore
import com.woowacourse.ody.domain.repository.JoinRepository
import com.woowacourse.ody.domain.repository.MeetingRepository

class JoinCompleteViewModelFactory(
    private val meetingRepository: MeetingRepository,
    private val joinRepository: JoinRepository,
    private val datastore: OdyDatastore,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(JoinCompleteViewModel::class.java)) {
            JoinCompleteViewModel(
                meetingRepository,
                joinRepository,
                datastore,
            ) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
