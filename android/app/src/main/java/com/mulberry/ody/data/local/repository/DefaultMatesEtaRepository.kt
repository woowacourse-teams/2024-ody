package com.mulberry.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.AlarmManagerHelper
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import java.time.LocalDateTime
import javax.inject.Inject

class DefaultMatesEtaRepository
    @Inject
    constructor(
        private val alarmManagerHelper: AlarmManagerHelper,
        private val matesEtaInfoDao: MateEtaInfoDao,
    ) : MatesEtaRepository {
        override fun reserveEtaFetchingJob(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
              alarmManagerHelper.reserveEtaDashboard(meetingId, meetingDateTime)
        }

        override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> =
            matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }

        override suspend fun clearEtaFetchingJob() {
            matesEtaInfoDao.deleteAll()
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)
    }
