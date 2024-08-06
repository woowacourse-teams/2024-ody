package com.woowacourse.ody.presentation.room.etadashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository

class EtaDashboardViewModel(
    private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModel() {
    private val _matesEta: LiveData<List<MateEta>> = matesEtaRepository.fetchMatesEta(meetingId = meetingId)
    val matesEta: LiveData<List<MateEta>> get() = _matesEta
}
