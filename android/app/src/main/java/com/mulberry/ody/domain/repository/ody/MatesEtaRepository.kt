package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.MateEtaInfo
import kotlinx.coroutines.flow.Flow

interface MatesEtaRepository {
    fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()

    suspend fun stopEtaDashboardService(meetingId: Long)

    suspend fun stopEtaDashboardService()
}
