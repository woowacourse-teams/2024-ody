package com.woowacourse.ody.domain.repository.ody

import androidx.lifecycle.LiveData
import com.woowacourse.ody.domain.model.MateEta

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(meetingId: Long, targetTimeMillisecond: Long)

    fun fetchMatesEta(meetingId: Long): LiveData<List<MateEta>>
}
