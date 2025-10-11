package com.mulberry.ody.domain.usecase

import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatesEtaInfoUseCase @Inject constructor(
    private val matesEtaRepository: MatesEtaRepository
) {
    operator fun invoke(meetingId: Long): Flow<MateEtaInfo?> {
        return matesEtaRepository.fetchMatesEtaInfo(meetingId)
    }
}
