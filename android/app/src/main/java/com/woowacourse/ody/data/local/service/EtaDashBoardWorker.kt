package com.woowacourse.ody.data.local.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.OdyApplication
import com.woowacourse.ody.domain.repository.ody.MeetingRepository
import java.util.concurrent.TimeUnit

class EtaDashBoardWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    private val meetingRepository: MeetingRepository by lazy { (applicationContext as OdyApplication).meetingRepository }
    private val meetingId: Long by lazy { workerParameters.inputData.getLong(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE) }

    override suspend fun doWork(): Result {
        if (meetingId == MEETING_ID_DEFAULT_VALUE) {
            return Result.failure()
        }

        // todo: 다음 이슈에서 아래 있는 것들은 직접 받아오기
        val isMissing = false
        val latitude = "39.123345"
        val longitude = "126.234524"

        val mateEtas = meetingRepository.patchMatesEta(meetingId, isMissing, latitude, longitude).getOrNull()
        return if (mateEtas != null) {
            val mateEtaResponses = mateEtas.map { MateEtaResponse(it.nickname, it.etaType, it.durationMinute) }
            Result.success(workDataOf(MATE_ETA_RESPONSE_KEY to mateEtaResponses.convertMateEtasToJson()))
        } else {
            Result.failure()
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
