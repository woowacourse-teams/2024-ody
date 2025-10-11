package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import javax.inject.Inject

class OpenEtaDashboardUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        private val matesEtaRepository: MatesEtaRepository,
    ) {
        suspend operator fun invoke(etaOpenInfo: EtaOpenInfo) {
            if (!authRepository.isLoggedIn() || !etaOpenInfo.meetingDateTime.isEtaOpenTime()) {
                return
            }
            matesEtaRepository.openEtaDashboard(etaOpenInfo)
        }
    }
