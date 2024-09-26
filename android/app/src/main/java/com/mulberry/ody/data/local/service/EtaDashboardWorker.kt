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
import androidx.work.workDataOf
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mulberry.ody.data.local.db.OdyDatastore
import com.mulberry.ody.data.local.entity.eta.MatesEtaInfoResponse
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.PermissionHelper
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
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
        private val odyDatastore: OdyDatastore,
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
            odyDatastore.setMeetingJobUUID(meetingId, id.toString())
            if (meetingId == MEETING_ID_DEFAULT_VALUE) return Result.failure()

            for (i in 0..durationTime step INTERVAL_MILLIS) {
                val mateEtaInfo = getLocation()
                val mateEtaResponses = mateEtaInfo?.toMateEtaInfoResponse()
                val data =
                    workDataOf(MATE_ETA_RESPONSE_KEY to mateEtaResponses?.convertMateEtasToJson())
                setProgress(data)
                delay(INTERVAL_MILLIS)
            }
            odyDatastore.removeMeetingJobUUID(meetingId)
            val mateEtaInfo = getLocation()
            val mateEtaResponses = mateEtaInfo?.toMateEtaInfoResponse()
            val data =
                workDataOf(MATE_ETA_RESPONSE_KEY to mateEtaResponses?.convertMateEtasToJson())
            return Result.success(data)
        }

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

        private fun MateEtaInfo.toMateEtaInfoResponse(): MatesEtaInfoResponse {
            return MatesEtaInfoResponse(userId, mateEtas)
        }

        private fun MatesEtaInfoResponse.convertMateEtasToJson(): String {
            val moshi =
                Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            val jsonAdapter = moshi.adapter(MatesEtaInfoResponse::class.java)
            return jsonAdapter.toJson(this)
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
            const val MATE_ETA_RESPONSE_KEY = "mate_eta_response"

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
