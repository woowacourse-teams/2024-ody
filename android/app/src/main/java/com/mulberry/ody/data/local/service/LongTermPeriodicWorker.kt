package com.mulberry.ody.data.local.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class LongTermPeriodicWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParams: WorkerParameters,
        private val periodicWork: PeriodicWork,
        private val longTermJobUUIDStore: LongTermJobUUIDStore,
    ) : CoroutineWorker(context, workerParams) {
        private val workId: Long by lazy {
            workerParams.inputData.getLong(
                WORK_ID_KEY,
                WORK_ID_DEFAULT_VALUE,
            )
        }

        private val durationTime: Long by lazy {
            workerParams.inputData.getLong(
                DURATION_TIME_MILLIS_KEY,
                DURATION_TIME_DEFAULT_VALUE,
            )
        }

        private val intervalTime: Long by lazy {
            workerParams.inputData.getLong(
                INTERVAL_TIME_MILLIS_KEY,
                INTERVAL_TIME_MILLIS_DEFAULT_VALUE,
            )
        }

        override suspend fun doWork(): Result {
            longTermJobUUIDStore.setJobUUID(workId, id.toString())
            for (i in 0..durationTime step intervalTime) {
                val result = periodicWork.doWork()
                val data = workDataOf(WORK_RESULT_KEY to result)
                setProgress(data)
                delay(intervalTime)
            }
            longTermJobUUIDStore.removeJobUUID(workId)

            val result = periodicWork.doWork()
            val data = workDataOf(WORK_RESULT_KEY to result)
            return Result.success(data)
        }

        companion object {
            private const val WORK_ID_KEY = "work_id"
            private const val WORK_ID_DEFAULT_VALUE = -1L
            private const val WORK_RESULT_KEY = "work_result"
            private const val SECOND_MILLIS = 1000L
            private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
            private const val DURATION_TIME_MILLIS_KEY = "duration_time"
            private const val DURATION_TIME_DEFAULT_VALUE = 10 * MINUTE_MILLIS
            private const val INTERVAL_TIME_MILLIS_KEY = "interval_time"
            private const val INTERVAL_TIME_MILLIS_DEFAULT_VALUE = 10 * SECOND_MILLIS
        }
    }
