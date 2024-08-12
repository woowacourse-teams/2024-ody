package com.woowacourse.ody.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woowacourse.ody.domain.model.EtaType
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository

object FakeMatesEtaRepository : MatesEtaRepository {
    override fun reserveEtaFetchingJob(
        meetingId: Long,
        targetTimeMillisecond: Long,
    ) = Unit

    override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> {
        val liveData = MutableLiveData<MateEtaInfo?>()
        val mateEtaInfo =
            MateEtaInfo(
                userNickname = "해음",
                mateEtas =
                    listOf(
                        MateEta("콜리", EtaType.LATE_WARNING, 83),
                        MateEta("올리브", EtaType.ARRIVAL_SOON, 10),
                        MateEta("카키", EtaType.ARRIVED, 0),
                        MateEta("해음", EtaType.MISSING, -1),
                    ),
            )
        liveData.value = mateEtaInfo
        return liveData
    }
}
