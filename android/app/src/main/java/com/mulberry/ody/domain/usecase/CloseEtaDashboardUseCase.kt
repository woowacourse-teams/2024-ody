package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import javax.inject.Inject

class CloseEtaDashboardUseCase
    @Inject
    constructor(
        private val matesEtaRepository: MatesEtaRepository,
    ) {
        suspend operator fun invoke(meetingId: Long) {
            return matesEtaRepository.closeEtaDashboard(meetingId)
        }
    }
