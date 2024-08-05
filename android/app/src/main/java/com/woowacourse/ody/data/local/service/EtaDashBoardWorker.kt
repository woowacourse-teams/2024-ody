package com.woowacourse.ody.data.local.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.woowacourse.ody.OdyApplication

class EtaDashBoardWorker(context: Context, private val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        val meetingRepository = (applicationContext as OdyApplication).meetingRepository
        val inputData = workerParameters.inputData
        val meetingId = inputData.getLong("meeting_id", -1L)

        // todo: 다음 이슈에서 아래 있는 것들은 직접 받아오기
//        val isMissing = inputData.getBoolean("isMissing", false)
//        val latitude = inputData.getString("latitude") ?: return Result.failure()
//        val longitude = inputData.getString("longitude") ?: return Result.failure()

        if (meetingId == -1L) {
            return Result.failure()
        }

        val mateEtas = meetingRepository.patchMatesEta(meetingId, false, "39.123345", "126.234524").getOrNull()
        return if (mateEtas != null) {
            val mateEtaResponses = mateEtas.map { MateEtaResponse(it.nickname, it.etaType, it.durationMinute) }
            Result.success(workDataOf("어쩌구" to mateEtaResponses.convertMateEtasToJson()))
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
}
