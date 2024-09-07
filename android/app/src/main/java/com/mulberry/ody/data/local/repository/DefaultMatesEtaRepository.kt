package com.mulberry.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulberry.ody.data.local.entity.eta.MatesEtaInfoResponse
import com.mulberry.ody.data.local.service.EtaDashboardWorker
import com.mulberry.ody.data.local.service.EtaDashboardWorker.Companion.MATE_ETA_RESPONSE_KEY
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class DefaultMatesEtaRepository(
    private val workManager: WorkManager,
) : MatesEtaRepository {
    override fun reserveEtaFetchingJob(
        meetingId: Long,
        targetTimeMillisecond: Long,
    ) {
        val currentTime = System.currentTimeMillis()
        val delay = targetTimeMillisecond - currentTime
        if (currentTime >= targetTimeMillisecond) {
            return
        }

        val workRequest = EtaDashboardWorker.getWorkRequest(meetingId = meetingId, delay = delay)
        workManager.enqueue(workRequest)
    }

    override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> {
        val tag = meetingId.toString()
        val liveData = workManager.getWorkInfosByTagLiveData(tag)
        return liveData.map { it.toMateEtas() }
    }

    private fun List<WorkInfo>.toMateEtas(): MateEtaInfo? {
        val recentWorkInfo = filter { it.state == WorkInfo.State.SUCCEEDED }.maxByOrNull { it.initialDelayMillis }
        return recentWorkInfo?.outputData?.getString(MATE_ETA_RESPONSE_KEY)?.convertMateEtasToJson()
    }

    private fun String.convertMateEtasToJson(): MateEtaInfo? {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val jsonAdapter = moshi.adapter(MatesEtaInfoResponse::class.java)
        val convertResult = jsonAdapter.fromJson(this) ?: return null
        return MateEtaInfo(convertResult.userId, convertResult.mateEtas)
    }
}
