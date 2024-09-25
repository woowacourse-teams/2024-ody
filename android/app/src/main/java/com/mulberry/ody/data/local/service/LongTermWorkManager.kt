package com.mulberry.ody.data.local.service

import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.mulberry.ody.presentation.join.toMilliSeconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LongTermWorkManager<T>
    @Inject
    constructor(
        private val workManager: WorkManager,
        val longTermJobUUIDStore: LongTermJobUUIDStore,
    ) {
        @OptIn(ExperimentalCoroutinesApi::class)
        fun getWorkProgressFlow(workId: Long): Flow<T?> =
            longTermJobUUIDStore.getJobUUID(workId).flatMapConcat { uuid ->
                if (uuid == null) {
                    throw Exception("uuid is null")
                } else {
                    workManager.getWorkInfoByIdFlow(uuid)
                }
            }.map {
                val data =
                    if (it.state == WorkInfo.State.SUCCEEDED) {
                        it.outputData
                    } else {
                        it.progress
                    }
                data.keyValueMap[WORK_RESULT_KEY] as T
            }

        fun enqueueLongTermJob(
            workId: Long,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime,
        ) {
            val startMillis = startDateTime.toMilliSeconds(LOCAL_ZONE_ID)
            val endMillis = endDateTime.toMilliSeconds(LOCAL_ZONE_ID)
            enqueueLongTermJob(workId, startMillis, endMillis)
        }

        private fun enqueueLongTermJob(
            workId: Long,
            startMillisecond: Long,
            endMillisecond: Long,
            intervalMilliSecond: Long = 10 * 60 * 1000L,
        ) {
            val initialInterval = endMillisecond - startMillisecond
            val initialRequest = getWorkRequest(workId, startMillisecond, initialInterval)
            if (initialInterval < intervalMilliSecond) return

            var continuation = workManager.beginWith(initialRequest)
            var currentMilliSecond = startMillisecond
            while (currentMilliSecond < endMillisecond) {
                val duration =
                    if (currentMilliSecond + intervalMilliSecond < endMillisecond) {
                        intervalMilliSecond
                    } else {
                        endMillisecond - currentMilliSecond
                    }
                val request = getWorkRequest(workId, currentMilliSecond, duration)
                continuation = continuation.then(request)
                currentMilliSecond += intervalMilliSecond
            }
            continuation.enqueue()
        }

        private fun getWorkRequest(
            workId: Long,
            startTimeMilliSecond: Long,
            durationMilliSecond: Long,
        ): OneTimeWorkRequest {
            val currentTime = System.currentTimeMillis()
            val initialDelay = startTimeMilliSecond - currentTime

            val workIdData = workDataOf(WORK_ID_KEY to workId)
            val durationData = workDataOf(DURATION_TIME_MILLIS_KEY to durationMilliSecond)
            return OneTimeWorkRequestBuilder<LongTermPeriodicWorker>()
                .setInputData(workIdData)
                .setInputData(durationData)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build()
        }

        companion object {
            const val WORK_ID_KEY = "work_id"
            const val DURATION_TIME_MILLIS_KEY = "duration_time"
            private const val WORK_RESULT_KEY = "work_result"
            private const val LOCAL_ZONE_ID = "Asia/Seoul"
        }
    }
