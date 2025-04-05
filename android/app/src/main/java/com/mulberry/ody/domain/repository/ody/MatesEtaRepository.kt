package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.MateEtaInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface MatesEtaRepository {
    fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()

    fun openEtaDashboard(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    )

    suspend fun closeEtaDashboard(meetingId: Long)

    suspend fun closeEtaDashboard()
}
