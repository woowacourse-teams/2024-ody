package com.mulberry.ody.fake

import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.mateEtaInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object FakeMatesEtaRepository : MatesEtaRepository {
    override fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?> {
        return flow { emit(mateEtaInfo) }
    }

    override suspend fun clearEtaFetchingJob() = Unit

    override suspend fun stopEtaDashboardService(meetingId: Long) = Unit

    override suspend fun stopEtaDashboardService() = Unit
}
