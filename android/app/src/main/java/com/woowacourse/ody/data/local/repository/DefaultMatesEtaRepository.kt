package com.woowacourse.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.OneTimeWorkRequestBuilder
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
        return workManager.getWorkInfosByTagLiveData(meetingId.toString()).map { workInfos ->
            val workInfoResults =
                workInfos
                    .filter { it.state == WorkInfo.State.SUCCEEDED }
                    .mapNotNull { it.outputData.getString(MATE_ETA_RESPONSE_KEY) }

            if (workInfoResults.isEmpty()) {
                listOf()
            } else {
                workInfoResults
                    .last()
                    .convertMateEtasToJson()
            }
        }
    }

    private fun String.convertMateEtasToJson(): List<MateEta> {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val type = Types.newParameterizedType(List::class.java, MateEtaResponse::class.java)
        val jsonAdapter = moshi.adapter<List<MateEtaResponse>>(type)
        return jsonAdapter.fromJson(this)
            ?.map { MateEta(it.nickname, it.etaType, it.durationMinute) } ?: listOf()
    }
}
