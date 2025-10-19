package com.mulberry.ody.data.repository

import android.content.Context
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.db.OdyDataStore
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.service.EtaDashboardService
import com.mulberry.ody.domain.common.toMilliSeconds
import com.mulberry.ody.domain.model.EtaOpenInfo
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
        private val odyDataStore: OdyDataStore,
    ) : MatesEtaRepository {
        override fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?> {
            return matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }
        }

        override suspend fun clearEtaFetchingJob() {
            matesEtaInfoDao.deleteAll()
        }

        override fun openEtaDashboard(etaOpenInfo: EtaOpenInfo) {
            val meetingTimeMills = etaOpenInfo.meetingDateTime.dateTime.toMilliSeconds()
            val serviceIntent = EtaDashboardService.getIntent(context, etaOpenInfo.meetingId, meetingTimeMills, isOpen = true)
            context.startForegroundService(serviceIntent)
        }

        override suspend fun closeEtaDashboard(meetingId: Long) {
            val serviceIntent = EtaDashboardService.getIntent(context, meetingId, isOpen = false)
            context.startForegroundService(serviceIntent)
        }

        override fun isFirstSeenEtaDashboard(): Flow<Boolean> {
            return odyDataStore.getIsFirstSeenEtaDashboard()
        }

        override suspend fun updateEtaDashboardSeen() {
            odyDataStore.updateEtaDashboardGuideSeen()
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)
    }
