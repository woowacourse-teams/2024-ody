package com.woowacourse.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.data.local.service.EtaDashBoardWorker
import com.woowacourse.ody.data.local.service.EtaDashBoardWorker.Companion.MATE_ETA_RESPONSE_KEY
import com.woowacourse.ody.data.local.service.MateEtaResponse
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository

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

        val workRequest = EtaDashBoardWorker.getWorkRequest(meetingId = meetingId, delay = delay)
        workManager.enqueue(workRequest)
    }

    override fun fetchMatesEta(meetingId: Long): LiveData<List<MateEta>> {
        val tag = meetingId.toString()
        val liveData = workManager.getWorkInfosByTagLiveData(tag)
        return liveData.map { it.toMateEtas() }
    }

    private fun List<WorkInfo>.toMateEtas(): List<MateEta> {
        val recentWorkInfo = findLast { it.state == WorkInfo.State.SUCCEEDED } ?: return emptyList()
        return recentWorkInfo.outputData.getString(MATE_ETA_RESPONSE_KEY)?.convertMateEtasToJson() ?: emptyList()
    }

    private fun String.convertMateEtasToJson(): List<MateEta> {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val type = Types.newParameterizedType(List::class.java, MateEtaResponse::class.java)
        val jsonAdapter = moshi.adapter<List<MateEtaResponse>>(type)
        return jsonAdapter.fromJson(this)?.map { MateEta(it.nickname, it.etaType, it.durationMinute) } ?: listOf()
    }
}
