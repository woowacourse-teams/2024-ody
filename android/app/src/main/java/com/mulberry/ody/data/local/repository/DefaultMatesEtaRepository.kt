package com.mulberry.ody.data.local.repository

import com.mulberry.ody.data.local.db.EtaReserveDao
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.data.local.entity.reserve.EtaReserveEntity
import com.mulberry.ody.data.local.service.EtaDashboardAlarm
import com.mulberry.ody.domain.common.toMilliSeconds
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.max

class DefaultMatesEtaRepository
@Inject
constructor(
    private val etaDashboardAlarm: EtaDashboardAlarm,
    private val matesEtaInfoDao: MateEtaInfoDao,
    private val etaReserveDao: EtaReserveDao,
) : MatesEtaRepository {
    override suspend fun reserveEtaFetchingJob(
        meetingId: Long,
        meetingDateTime: LocalDateTime,
    ) {
        val openMillis = meetingDateTime.etaDashboardOpenMillis()
        val openReserveId = saveEtaReservation(meetingId, openMillis, isOpen = true)
        etaDashboardAlarm.reserveEtaDashboardOpen(meetingId, openMillis, openReserveId)

        val closeMillis = meetingDateTime.etaDashboardCloseMillis()
        val closeReserveId = saveEtaReservation(meetingId, openMillis, isOpen = false)
        etaDashboardAlarm.reserveEtaDashboardClose(meetingId, closeMillis, closeReserveId)
    }

    private suspend fun saveEtaReservation(meetingId: Long, reserveMillis: Long, isOpen: Boolean): Long {
        val entity = EtaReserveEntity(meetingId, reserveMillis, isOpen)
        return etaReserveDao.save(entity)
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

    override suspend fun deleteEtaReservation(reserveId: Long) {
        etaReserveDao.delete(reserveId)
    }

    override suspend fun clearEtaReservation() {
        val etaReserveEntities = etaReserveDao.fetchAll()
        etaReserveEntities.forEach { etaReserveEntity ->
            etaDashboardAlarm.cancelEtaDashboard(
                etaReserveEntity.meetingId,
                etaReserveEntity.id,
                etaReserveEntity.isOpen,
            )
        }
    }

    private fun MateEtaInfoEntity.toMateEtaInfo(): MateEtaInfo = MateEtaInfo(mateId, mateEtas)

    companion object {
        private const val ETA_OPEN_MINUTE = 30L
        private const val ETA_CLOSE_MINUTE = 2L
    }
}
