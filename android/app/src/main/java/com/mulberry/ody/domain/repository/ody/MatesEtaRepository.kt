package com.mulberry.ody.domain.repository.ody

import androidx.lifecycle.LiveData
import com.mulberry.ody.domain.model.MateEtaInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    )

    fun fetchMatesEta(meetingId: Long): Flow<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()
}
