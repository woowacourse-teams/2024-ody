package com.mulberry.ody.domain.repository.ody

import com.mulberry.ody.domain.model.MateEtaInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface MatesEtaRepository {
    suspend fun reserveEtaFetchingJob(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    )

    fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()

    suspend fun deleteEtaReservation(meetingId: Long)

    suspend fun clearEtaReservation(isReservationPending: Boolean)

    suspend fun reserveAllEtaReservation()
}
