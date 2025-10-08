package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.EtaOpenInfo
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.model.MeetingDateTime
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface MatesEtaRepository {
    fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()

    fun openEtaDashboard(etaOpenInfo: EtaOpenInfo)

    suspend fun closeEtaDashboard(meetingId: Long)

    suspend fun closeEtaDashboard()

    fun isFirstSeenEtaDashboard(): Flow<Boolean>

    suspend fun updateEtaDashboardSeen()
}
