package com.mulberry.ody.domain.repository.ody

import androidx.lifecycle.LiveData
import com.mulberry.ody.domain.model.MateEtaInfo

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(
        meetingId: Long,
        targetTimeMillisecond: Long,
    )

    fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?>
}
