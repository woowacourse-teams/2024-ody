package com.mulberry.ody.fake

import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.mateEtaInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

object FakeMatesEtaRepository : MatesEtaRepository {
    override fun reserveEtaFetchingJob(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) = Unit

    override fun fetchMatesEta(meetingId: Long): Flow<MateEtaInfo?> {
        return flow { emit(mateEtaInfo) }
    }

    override suspend fun clearEtaFetchingJob() = Unit
}
