package com.mulberry.ody.domain.repository.ody

import androidx.lifecycle.LiveData
import com.mulberry.ody.domain.model.MateEtaInfo
import java.time.LocalDateTime

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    )

    fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()
}
