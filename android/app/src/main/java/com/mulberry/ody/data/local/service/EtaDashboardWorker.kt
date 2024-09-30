package com.mulberry.ody.data.local.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.GpsHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@HiltWorker
class EtaDashboardWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParameters: WorkerParameters,
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
        private val mateEtaInfoDao: MateEtaInfoDao,
        private val gpsHelper: GpsHelper,
    ) : CoroutineWorker(context, workerParameters) {
        private val meetingId: Long by lazy {
            workerParameters.inputData.getLong(
                MEETING_ID_KEY,
                MEETING_ID_DEFAULT_VALUE,
            )
        }

        private val durationTime: Long by lazy {
            workerParameters.inputData.getLong(
                DURATION_KEY,
                DURATION_DEFAULT_VALUE,
            )
        }

        override suspend fun doWork(): Result {
            if (meetingId == MEETING_ID_DEFAULT_VALUE) return Result.failure()
            for (i in 0..durationTime step INTERVAL_MILLIS) {
                val mateEtaInfo = getLocation()
                if (mateEtaInfo != null) {
                    val mateEtaInfoEntity =
                        MateEtaInfoEntity(meetingId, mateEtaInfo.userId, mateEtaInfo.mateEtas)
                    mateEtaInfoDao.upsert(mateEtaInfoEntity)
                }
                delay(INTERVAL_MILLIS)
            }
            return Result.success()
        }

        private suspend fun getLocation(): MateEtaInfo? {
            val location = gpsHelper.getCurrentCoordinate()

            return if (location == null) {
                updateMatesEta(true, "0.0", "0.0")
            } else {
                updateMatesEta(false, location.latitude.toString(), location.longitude.toString())
            }
        }

        private suspend fun updateMatesEta(
            isMissing: Boolean,
            latitude: String,
            longitude: String,
        ): MateEtaInfo? {
            return meetingRepository.patchMatesEta(meetingId, isMissing, latitude, longitude)
                .onFailure { analyticsHelper.logNetworkErrorEvent(TAG, it.message) }
                .getOrNull()
        }

        companion object {
            private const val TAG = "EtaDashboardWorker"
            private const val MEETING_ID_KEY = "meeting_id"
            private const val MEETING_ID_DEFAULT_VALUE = -1L
            private const val MAX_MINUTE = 10L
            private const val MILLIS = 1000L
            private const val INTERVAL_MILLIS = 10 * MILLIS
            private const val DURATION_KEY = "duration"
            private const val DURATION_DEFAULT_VALUE = MAX_MINUTE * 60 * MILLIS

            fun getWorkRequest(
                meetingId: Long,
                duration: Long,
                initialDelay: Long,
            ): OneTimeWorkRequest {
                val inputData =
                    Data.Builder()
                        .putLong(MEETING_ID_KEY, meetingId)
                        .putLong(DURATION_KEY, duration)
                        .build()

                return OneTimeWorkRequestBuilder<EtaDashboardWorker>()
                    .setInputData(inputData)
                    .addTag(meetingId.toString())
                    .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                    .build()
            }
        }
    }
