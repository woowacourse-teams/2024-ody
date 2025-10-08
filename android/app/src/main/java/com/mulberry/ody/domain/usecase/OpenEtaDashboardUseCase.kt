package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.model.MeetingDateTime
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import javax.inject.Inject

class OpenEtaDashboardUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val matesEtaRepository: MatesEtaRepository,
) {
    suspend operator fun invoke(
        meetingId: Long,
        meetingDateTime: MeetingDateTime,
    ) {
        if (!authRepository.isLoggedIn() || !meetingDateTime.isEtaOpenTime()) {
            return
        }
        matesEtaRepository.openEtaDashboard(meetingId, meetingDateTime)
    }
}
