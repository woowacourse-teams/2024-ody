package com.mulberry.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.EtaDashboardWorker
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import javax.inject.Inject
import kotlin.math.min

class DefaultMatesEtaRepository
    @Inject
    constructor(
        private val workManager: WorkManager,
        private val matesEtaInfoDao: MateEtaInfoDao,
    ) : MatesEtaRepository {
        override fun reserveEtaFetchingJob(
            meetingId: Long,
            startMillisecond: Long,
            endMillisecond: Long,
            interval: Long,
        ) {
            val initialDuration = min(interval, endMillisecond - startMillisecond)
            val initialRequest = getEtaFetchingRequest(meetingId, initialDuration, startMillisecond)

            var workContnuation = workManager.beginWith(initialRequest)
            var currentMilliSecond = startMillisecond + interval
            while (currentMilliSecond < endMillisecond) {
                val duration = min(interval, endMillisecond - currentMilliSecond)
                val workRequest = getEtaFetchingRequest(meetingId, duration, currentMilliSecond)
                workContnuation = workContnuation.then(workRequest)
                currentMilliSecond += interval
            }
            workContnuation.enqueue()
        }

        private fun getEtaFetchingRequest(
            meetingId: Long,
            workDuration: Long,
            targetTimeMillisecond: Long,
        ): OneTimeWorkRequest {
            val currentTime = System.currentTimeMillis()
            val initialDelay = targetTimeMillisecond - currentTime

            return EtaDashboardWorker.getWorkRequest(meetingId, workDuration, initialDelay)
        }

        override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> =
            matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }

        override fun clearEtaFetchingJob() {
            workManager.cancelAllWork()
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)
    }
