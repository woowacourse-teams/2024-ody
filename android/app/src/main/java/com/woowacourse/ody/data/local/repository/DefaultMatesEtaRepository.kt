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
import com.woowacourse.ody.data.local.service.MateEtaInfoResponse
import com.woowacourse.ody.domain.model.MateEtaInfo
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

    override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> {
        val tag = meetingId.toString()
        val liveData = workManager.getWorkInfosByTagLiveData(tag)
        return liveData.map { it.toMateEtas() }
    }

    private fun List<WorkInfo>.toMateEtas(): MateEtaInfo? {
        val recentWorkInfo = findLast { it.state == WorkInfo.State.SUCCEEDED }
        return recentWorkInfo?.outputData?.getString(MATE_ETA_RESPONSE_KEY)?.convertMateEtasToJson()
    }

    private fun String.convertMateEtasToJson(): MateEtaInfo? {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val jsonAdapter = moshi.adapter(MateEtaInfoResponse::class.java)
        val convertResult = jsonAdapter.fromJson(this) ?: return null
        return MateEtaInfo(convertResult.userNickname, convertResult.mateEtas)
    }
}
