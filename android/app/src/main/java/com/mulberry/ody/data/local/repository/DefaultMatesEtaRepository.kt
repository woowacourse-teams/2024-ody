package com.mulberry.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkManager
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.EtaDashboardWorker
import com.mulberry.ody.domain.common.toMilliSeconds
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class DefaultMatesEtaRepository
    @Inject
    constructor(
        private val workManager: WorkManager,
        private val matesEtaInfoDao: MateEtaInfoDao,
    ) : MatesEtaRepository {
        override fun reserveEtaFetchingJob(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            val initialTime = meetingDateTime.minusMinutes(BEFORE_MINUTE).toMilliSeconds()
            val endTime = meetingDateTime.plusMinutes(AFTER_MINUTE).toMilliSeconds()
            val nowTime = System.currentTimeMillis()

            val startTime = max(initialTime, nowTime)
            val reserveCount = ((endTime - startTime) / EtaDashboardWorker.RESERVE_INTERVAL).toInt()
            val delayTime = startTime - nowTime

            val etaDashboardWorker = EtaDashboardWorker.getWorkRequest(meetingId, delayTime, reserveCount)
            workManager.enqueue(etaDashboardWorker)
        }

        override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> =
            matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }

        override suspend fun clearEtaFetchingJob() {
            workManager.cancelAllWork()
            matesEtaInfoDao.deleteAll()
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)

        companion object {
            private const val BEFORE_MINUTE = 30L
            private const val AFTER_MINUTE = 1L
        }
    }
