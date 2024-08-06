package com.woowacourse.ody.data.local.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.android.gms.location.LocationServices
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.domain.model.MateEta
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resumeWithException

class EtaDashBoardWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val meetingRepository: MeetingRepository by lazy { (applicationContext as OdyApplication).meetingRepository }
    private val meetingId: Long by lazy {
        workerParameters.inputData.getLong(
            MEETING_ID_KEY,
            MEETING_ID_DEFAULT_VALUE,
        )
    }

    override suspend fun doWork(): Result {
        if (meetingId == MEETING_ID_DEFAULT_VALUE) {
            return Result.failure()
        }

        val mateEtas = getLocation()

        return if (mateEtas != null) {
            val mateEtaResponses =
                mateEtas.map { MateEtaResponse(it.nickname, it.etaType, it.durationMinute) }
            Result.success(workDataOf(MATE_ETA_RESPONSE_KEY to mateEtaResponses.convertMateEtasToJson()))
        } else {
            Result.failure()
        }
    }

    private suspend fun getLocation(): List<MateEta>? {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        if (ActivityCompat.checkSelfPermission(
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
        ) {
            meetingRepository.patchMatesEta(meetingId, true, "0.0", "0.0").getOrNull()
            return null
        }

        val location =
            suspendCancellableCoroutine { continuation ->
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        continuation.resume(location) {
                            if (location.latitude == 0.0 && location.longitude == 0.0) {
                                return@resume
                            }
                        }
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

        return if (location != null) {
            meetingRepository.patchMatesEta(meetingId, false, location.latitude.toString(), location.longitude.toString()).getOrNull()
        } else {
            meetingRepository.patchMatesEta(meetingId, false, "0.0", "0.0").getOrNull()
        }
    }

    private fun List<MateEtaResponse>.convertMateEtasToJson(): String {
        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        val type = Types.newParameterizedType(List::class.java, MateEtaResponse::class.java)
        val jsonAdapter = moshi.adapter<List<MateEtaResponse>>(type)
        return jsonAdapter.toJson(this)
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
