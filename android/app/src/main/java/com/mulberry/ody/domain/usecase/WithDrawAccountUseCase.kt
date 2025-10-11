package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.apiresult.ApiResult
import com.mulberry.ody.domain.repository.ody.AuthRepository
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import javax.inject.Inject

class WithDrawAccountUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val matesEtaRepository: MatesEtaRepository
) {
    suspend operator fun invoke(): ApiResult<Unit> {
        return authRepository.withdrawAccount().also {
            matesEtaRepository.clearEtaFetchingJob()
        }
    }
}
