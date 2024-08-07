package com.woowacourse.ody.presentation.room.etadashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.presentation.room.etadashboard.model.MateEtaUiModel
import com.woowacourse.ody.presentation.room.etadashboard.model.toMateEtaUiModels

class EtaDashboardViewModel(
    private val meetingId: Long,
    private val matesEtaRepository: MatesEtaRepository,
) : ViewModel() {
    private val matesEta: LiveData<MateEtaInfo?> = matesEtaRepository.fetchMatesEta(meetingId = meetingId)
    val mateEtaUiModels: LiveData<List<MateEtaUiModel>?> =
        matesEta.map {
            val mateEtaInfo = it ?: return@map null
            mateEtaInfo.mateEtas.toMateEtaUiModels(mateEtaInfo.userNickname)
        }
}
