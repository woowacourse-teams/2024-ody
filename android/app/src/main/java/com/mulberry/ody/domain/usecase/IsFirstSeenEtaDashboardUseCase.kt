package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsFirstSeenEtaDashboardUseCase @Inject constructor(
    private val matesEtaRepository: MatesEtaRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return matesEtaRepository.isFirstSeenEtaDashboard()
    }
}
