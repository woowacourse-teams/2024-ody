package com.mulberry.ody.domain.repository.ody

import androidx.lifecycle.LiveData
import com.mulberry.ody.domain.model.MateEtaInfo

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(
        meetingId: Long,
        startMillisecond: Long,
        endMillisecond: Long,
        interval: Long,
    )

    fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?>

    suspend fun clearEtaFetchingJob()
}
