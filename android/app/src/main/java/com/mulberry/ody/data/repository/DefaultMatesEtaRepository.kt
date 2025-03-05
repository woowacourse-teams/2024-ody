package com.mulberry.ody.data.repository

import android.content.Context
import com.mulberry.ody.data.local.db.EtaReservationDao
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.entity.reserve.EtaReservationEntity
import com.mulberry.ody.data.local.service.EtaDashboardAlarm
import com.mulberry.ody.data.local.service.EtaDashboardService
import com.mulberry.ody.domain.common.toMilliSeconds
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class DefaultMatesEtaRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val etaDashboardAlarm: EtaDashboardAlarm,
        private val matesEtaInfoDao: MateEtaInfoDao,
        private val etaReservationDao: EtaReservationDao,
    ) : MatesEtaRepository {
        override suspend fun reserveEtaFetchingJob(
            meetingId: Long,
            meetingDateTime: LocalDateTime,
        ) {
            val openMillis = meetingDateTime.etaDashboardOpenMillis()
            val openReservationId = saveEtaReservation(meetingId, openMillis, isOpen = true)
            etaDashboardAlarm.reserve(meetingId, openMillis, true, openReservationId)

            val closeMillis = meetingDateTime.etaDashboardCloseMillis()
            val closeReservationId = saveEtaReservation(meetingId, closeMillis, isOpen = false)
            etaDashboardAlarm.reserve(meetingId, closeMillis, false, closeReservationId)
        }

        private suspend fun saveEtaReservation(
            meetingId: Long,
            reserveMillis: Long,
            isOpen: Boolean,
        ): Long {
            val entity = EtaReservationEntity(meetingId, reserveMillis, isOpen)
            return etaReservationDao.save(entity)
        }

        private fun LocalDateTime.etaDashboardOpenMillis(): Long {
            val openMillis = minusMinutes(ETA_OPEN_MINUTE).toMilliSeconds()
            val nowMillis = System.currentTimeMillis()
            return max(openMillis, nowMillis)
        }

        private fun LocalDateTime.etaDashboardCloseMillis(): Long {
            return plusMinutes(ETA_CLOSE_MINUTE).toMilliSeconds()
        }

        override fun fetchMatesEtaInfo(meetingId: Long): Flow<MateEtaInfo?> =
            matesEtaInfoDao.getMateEtaInfo(meetingId).map { it?.toMateEtaInfo() }

        override suspend fun clearEtaFetchingJob() {
            matesEtaInfoDao.deleteAll()
        }

        override suspend fun deleteEtaReservation(meetingId: Long) {
            etaDashboardAlarm.cancelByMeetingId(meetingId)
            etaReservationDao.delete(meetingId)
            val serviceIntent = EtaDashboardService.getIntent(context, meetingId, isOpen = false)
            context.startForegroundService(serviceIntent)
        }

        override suspend fun clearEtaReservation(isReservationPending: Boolean) {
            etaDashboardAlarm.cancelAll()
            if (!isReservationPending) {
                etaReservationDao.deleteAll()
            }
            val serviceIntent = EtaDashboardService.getIntent(context)
            context.stopService(serviceIntent)
        }

        override suspend fun reserveAllEtaReservation() {
            val entities = etaReservationDao.fetchAll()
            entities.forEach { entity ->
                etaDashboardAlarm.reserve(
                    entity.meetingId,
                    max(entity.reserveMillis, System.currentTimeMillis() + ETA_RESERVE_MILLIS_DELAY),
                    entity.isOpen,
                    entity.id,
                )
            }
        }

        private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)

        companion object {
            private const val ETA_RESERVE_MILLIS_DELAY = 3 * 1000
            private const val ETA_OPEN_MINUTE = 30L
            private const val ETA_CLOSE_MINUTE = 2L
        }
    }
