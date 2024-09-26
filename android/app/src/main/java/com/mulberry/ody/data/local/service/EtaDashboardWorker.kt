package com.mulberry.ody.data.local.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

@HiltWorker
class EtaDashboardWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted workerParameters: WorkerParameters,
        private val analyticsHelper: AnalyticsHelper,
        private val meetingRepository: MeetingRepository,
        private val permissionHelper: PermissionHelper,
        private val mateEtaInfoDao: MateEtaInfoDao,
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

        @OptIn(ExperimentalCoroutinesApi::class)
        @SuppressLint("MissingPermission")
        private suspend fun getLocation(): MateEtaInfo? {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(applicationContext)

            if (hasLocationPermissions().not() || !isLocationEnabled()) {
                return updateMatesEta(true, "0.0", "0.0")
            }

            val currentLocationRequest =
                CurrentLocationRequest.Builder()
                    .setDurationMillis(30 * MILLIS)
                    .setMaxUpdateAgeMillis(60 * MILLIS)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()

            val location =
                suspendCancellableCoroutine { continuation ->
                    fusedLocationProviderClient.getCurrentLocation(currentLocationRequest, null)
                        .addOnSuccessListener { location ->
                            continuation.resume(location) {
                                Timber.d("${location.latitude} ${location.longitude}")
                            }
                        }.addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }

            return updateMatesEta(false, location.latitude.toString(), location.longitude.toString())
        }

        private fun isLocationEnabled(): Boolean {
            val locationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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

        private fun hasLocationPermissions(): Boolean {
            return permissionHelper.hasFineLocationPermission() &&
                permissionHelper.hasCoarseLocationPermission() &&
                permissionHelper.hasBackgroundLocationPermission()
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
