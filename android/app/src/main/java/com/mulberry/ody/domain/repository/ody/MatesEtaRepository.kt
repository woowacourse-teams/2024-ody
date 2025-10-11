package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MateEtaInfo
import kotlinx.coroutines.flow.Flow

interface MatesEtaRepository {
    fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()

    fun openEtaDashboard(etaOpenInfo: EtaOpenInfo)

    suspend fun closeEtaDashboard(meetingId: Long)

    fun isFirstSeenEtaDashboard(): Flow<Boolean>

    suspend fun updateEtaDashboardSeen()
}
