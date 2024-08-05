package com.woowacourse.ody.data.local.repository

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.woowacourse.ody.data.local.service.EtaDashBoardWorker
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import java.util.concurrent.TimeUnit

class DefaultMatesEtaRepository(
    private val workManager: WorkManager
) : MatesEtaRepository {
    override fun reserveEtaFetchingJob(meetingId: Long, targetTimeMillisecond: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = targetTimeMillisecond - currentTime

        if (currentTime >= targetTimeMillisecond) {
            return
        }

        val workRequest = OneTimeWorkRequestBuilder<EtaDashBoardWorker>()
            .addTag(meetingId.toString())
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(workRequest)
    }
}
