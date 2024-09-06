package com.ydo.ody.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ydo.ody.domain.model.MateEtaInfo
import com.ydo.ody.domain.repository.ody.MatesEtaRepository
import com.ydo.ody.mateEtaInfo

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
