package com.mulberry.ody.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.mateEtaInfo

object FakeMatesEtaRepository : MatesEtaRepository {
    override fun reserveEtaFetchingJob(
        meetingId: Long,
        targetTimeMillisecond: Long,
    ) = Unit

    override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> {
        val liveData = MutableLiveData<MateEtaInfo?>()
        liveData.value = mateEtaInfo
        return liveData
    }
}
