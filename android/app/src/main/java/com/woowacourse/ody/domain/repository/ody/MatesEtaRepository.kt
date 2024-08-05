package com.woowacourse.ody.domain.repository.ody

interface MatesEtaRepository {
    fun reserveEtaFetchingJob(meetingId: Long, targetTimeMillisecond: Long)
}
