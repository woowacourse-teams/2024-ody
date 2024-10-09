package com.mulberry.ody.data.local.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkManager
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.AlarmManagerHelper
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
        private val alarmManagerHelper: AlarmManagerHelper,
        private val workManager: WorkManager,
        private val matesEtaInfoDao: MateEtaInfoDao,
    ) : MatesEtaRepository {
        override fun reserveEtaFetchingJob(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            Log.e("TEST", "reserveEtaFetchingJob meetingId $meetingId, meetingDateTime $meetingDateTime")
            val initialTime = meetingDateTime.minusMinutes(BEFORE_MINUTE).toMilliSeconds()
            val endTime = meetingDateTime.plusMinutes(AFTER_MINUTE).toMilliSeconds()
            val nowTime = System.currentTimeMillis()

            val startTime = max(initialTime, nowTime)
            val reserveCount = ((endTime - startTime) / EtaDashboardWorker.RESERVE_INTERVAL).toInt()
            if (initialTime != startTime) { // 현재 시간이 약속 시간 30분 이후인 경우 바로 작업을 수행
                Log.e("TEST", "현재 시간이 약속 시간 30분 이후임! reserveCount $reserveCount")
                val etaDashboardWorker = EtaDashboardWorker.getWorkRequest(meetingId, reserveCount)
                workManager.enqueue(etaDashboardWorker)
                return
            }

            Log.e("TEST", "현재 시간이 약속 시간 30분 이전임! reserveCount $reserveCount")
            alarmManagerHelper.reserveEtaDashboardOpen(meetingId, startTime, reserveCount)
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
