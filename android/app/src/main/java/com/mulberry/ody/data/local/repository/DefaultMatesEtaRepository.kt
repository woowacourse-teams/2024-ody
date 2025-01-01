package com.mulberry.ody.data.local.repository

import android.content.Context
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.EtaDashboardService
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultMatesEtaRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val matesEtaInfoDao: MateEtaInfoDao,
    ) : MatesEtaRepository {
        override fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?> {
            return matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }
        }

        override suspend fun clearEtaFetchingJob() {
            matesEtaInfoDao.deleteAll()
        }

        override suspend fun stopEtaDashboardService(meetingId: Long) {
            val serviceIntent = EtaDashboardService.getIntent(context, meetingId, isOpen = false)
            context.startForegroundService(serviceIntent)
        }

        override suspend fun stopEtaDashboardService() {
            val serviceIntent = EtaDashboardService.getIntent(context)
            context.stopService(serviceIntent)
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)
    }
