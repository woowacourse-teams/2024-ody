package com.mulberry.ody.data.local.service

import android.content.Context
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class EtaDashboardWorker
    @AssistedInject
    constructor(
        @Assisted private val context: Context,
        @Assisted workerParameters: WorkerParameters,
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
        private val mateEtaInfoDao: MateEtaInfoDao,
        private val geoLocationHelper: LocationHelper,
    ) : CoroutineWorker(context, workerParameters) {
        private val meetingId: Long by lazy {
            workerParameters.inputData.getLong(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        }

        private val reserveCount: Int by lazy {
            workerParameters.inputData.getInt(RESERVE_COUNT_KEY, RESERVE_COUNT_DEFAULT_VALUE)
        }

        override suspend fun doWork(): Result {
            if (meetingId == MEETING_ID_DEFAULT_VALUE) return Result.failure()
            if (reserveCount == RESERVE_COUNT_DEFAULT_VALUE) return Result.failure()

            repeat(reserveCount) {
                Log.e("TEST", "doWork() for, meetingId: $meetingId reserveCount: $it")
//                val mateEtaInfo = getLocation()
//                if (mateEtaInfo != null) {
//                    val mateEtaInfoEntity = MateEtaInfoEntity(meetingId, mateEtaInfo.userId, mateEtaInfo.mateEtas)
//                    mateEtaInfoDao.upsert(mateEtaInfoEntity)
//                }
                delay(RESERVE_INTERVAL)
            }
            return Result.success()
        }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        val notification =
            NotificationCompat.Builder(context, "channelId")
                .setContentTitle(context.getString(R.string.app_name))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText("친구에게 도착 정보를 공유하기 위해 위치를 수집하고 있어요")
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setVibrate(longArrayOf(1, 1000))
                .build()

        return ForegroundInfo(0, notification)
    }

        private suspend fun getLocation(): MateEtaInfo? {
            return geoLocationHelper.getCurrentCoordinate().fold(
                onSuccess = { location ->
                    updateMatesEta(false, location.latitude.toString(), location.longitude.toString())
                },
                onFailure = {
                    updateMatesEta(true)
                },
            )
        }

        private suspend fun updateMatesEta(
            isMissing: Boolean,
            latitude: String = DEFAULT_LATITUDE,
            longitude: String = DEFAULT_LONGITUDE,
        ): MateEtaInfo? {
            return meetingRepository.patchMatesEta(meetingId, isMissing, latitude, longitude)
                .onFailure { analyticsHelper.logNetworkErrorEvent(TAG, it.message) }
                .getOrNull()
        }

        companion object {
            private const val TAG = "EtaDashboardWorker"
            const val RESERVE_INTERVAL = 10 * 1000L

            private const val MEETING_ID_KEY = "meeting_id"
            private const val MEETING_ID_DEFAULT_VALUE = -1L

            private const val RESERVE_COUNT_KEY = "reserve_count"
            private const val RESERVE_COUNT_DEFAULT_VALUE = -1

            private const val DEFAULT_LATITUDE = "0.0"
            private const val DEFAULT_LONGITUDE = "0.0"

            fun getWorkRequest(
                meetingId: Long,
                reserveCount: Int,
            ): OneTimeWorkRequest {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .setRequiresCharging(false)
                    .setRequiresDeviceIdle(false)
                    .setRequiresStorageNotLow(false)
                    .build()

                val inputData =
                    Data.Builder()
                        .putLong(MEETING_ID_KEY, meetingId)
                        .putInt(RESERVE_COUNT_KEY, reserveCount)
                        .build()

                return OneTimeWorkRequestBuilder<EtaDashboardWorker>()
                    .setInputData(inputData)
                    .addTag(meetingId.toString())
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setConstraints(constraints)
                    .build()
            }
        }
    }
