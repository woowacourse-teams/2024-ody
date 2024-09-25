package com.mulberry.ody.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.entity.eta.MatesEtaInfoResponse
import com.mulberry.ody.data.local.service.EtaDashboardWorker
import com.mulberry.ody.data.local.service.EtaDashboardWorker.Companion.MATE_ETA_RESPONSE_KEY
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultMatesEtaRepository
    @Inject
    constructor(
        private val workManager: WorkManager,
        private val odyDatastore: OdyDatastore,
    ) : MatesEtaRepository {
        private fun getEtaFetchingRequest(
            meetingId: Long,
            targetTimeMillisecond: Long,
        ): OneTimeWorkRequest {
            val currentTime = System.currentTimeMillis()
            val delay = targetTimeMillisecond - currentTime

            return EtaDashboardWorker.getWorkRequest(meetingId = meetingId, delay = delay)
        }

        override fun reserveEtaFetchingJob(
            meetingId: Long,
            startMillisecond: Long,
            endMillisecond: Long,
            interval: Long,
        ) {
            val initialRequest = getEtaFetchingRequest(meetingId, startMillisecond)
            var continuation = workManager.beginWith(initialRequest)
            var currentMilliSecond = startMillisecond
            while (currentMilliSecond < endMillisecond) {
                currentMilliSecond += interval
                val request = getEtaFetchingRequest(meetingId, currentMilliSecond)
                continuation = continuation.then(request)
            }
            continuation.enqueue()
        }

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun fetchMatesEta(meetingId: Long): LiveData<MateEtaInfo?> =
            odyDatastore.getMeetingJobUUID(meetingId).flatMapConcat { workUUID ->
                if (workUUID == null) {
                    emptyFlow()
                } else {
                    workManager.getWorkInfoByIdFlow(workUUID)
                }
            }.map { it.toMateEta() }.asLiveData()

        private fun WorkInfo.toMateEta(): MateEtaInfo? {
            val data =
                if (this.state == WorkInfo.State.SUCCEEDED) {
                    this.outputData
                } else {
                    this.progress
                }
            return data.getString(MATE_ETA_RESPONSE_KEY)?.convertJsonToMateEtaInfo()
        }

        private fun String.convertJsonToMateEtaInfo(): MateEtaInfo? {
            val moshi =
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            val jsonAdapter = moshi.adapter(MatesEtaInfoResponse::class.java)
            val convertResult = jsonAdapter.fromJson(this) ?: return null
            return MateEtaInfo(convertResult.userId, convertResult.mateEtas)
        }
    }
