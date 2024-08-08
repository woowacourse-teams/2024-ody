package com.woowacourse.ody.data.local.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.domain.model.MateEtaInfo
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

class EtaDashBoardWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val meetingRepository: MeetingRepository by lazy { (applicationContext as OdyApplication).meetingRepository }
    private val meetingId: Long by lazy { workerParameters.inputData.getLong(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE) }

    override suspend fun doWork(): Result {
        if (meetingId == MEETING_ID_DEFAULT_VALUE) {
            return Result.failure()
        }

        val mateEtaInfo = getLocation() ?: return Result.failure()
        val mateEtaResponses = mateEtaInfo.toMateEtaInfoResponse()
        return Result.success(workDataOf(MATE_ETA_RESPONSE_KEY to mateEtaResponses.convertMateEtasToJson()))
    }

    @SuppressLint("MissingPermission")
    private suspend fun getLocation(): MateEtaInfo? {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (checkLocationPermissions() || !isLocationEnabled()) {
            return updateMatesEta(true, "0.0", "0.0")
        }

        val location =
            suspendCancellableCoroutine { continuation ->
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location) {
                            Timber.d("${location.latitude} ${location.longitude}")
                        }
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

        if (location.latitude == 0.0 && location.longitude == 0.0) {
            return updateMatesEta(true, "0.0", "0.0")
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
            .getOrNull()
    }

    private fun MateEtaInfo.toMateEtaInfoResponse(): MateEtaInfoResponse {
        return MateEtaInfoResponse(userNickname, mateEtas)
    }

    private fun MateEtaInfoResponse.convertMateEtasToJson(): String {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val jsonAdapter = moshi.adapter(MateEtaInfoResponse::class.java)
        return jsonAdapter.toJson(this)
    }

    private fun checkLocationPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
        } else {
            return ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) != PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L
        const val MATE_ETA_RESPONSE_KEY = "mate_eta_response"

        fun getWorkRequest(
            meetingId: Long,
            delay: Long,
        ): WorkRequest {
            val inputData =
                Data.Builder()
                    .putLong(MEETING_ID_KEY, meetingId)
                    .build()

            return OneTimeWorkRequestBuilder<EtaDashBoardWorker>()
                .setInputData(inputData)
                .addTag(meetingId.toString())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()
        }
    }
}
