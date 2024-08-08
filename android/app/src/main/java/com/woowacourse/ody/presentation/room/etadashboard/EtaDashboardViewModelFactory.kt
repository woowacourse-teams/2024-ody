package com.woowacourse.ody.presentation.room.etadashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository

class EtaDashboardViewModelFactory(
    private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(EtaDashboardViewModel::class.java)) {
            EtaDashboardViewModel(meetingId, matesEtaRepository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
