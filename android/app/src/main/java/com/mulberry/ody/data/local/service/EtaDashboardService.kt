package com.mulberry.ody.data.local.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mulberry.ody.domain.apiresult.getOrNull
import com.mulberry.ody.domain.apiresult.onNetworkError
import com.mulberry.ody.domain.apiresult.suspendFold
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EtaDashboardService : Service() {
    @Inject
    lateinit var analyticsHelper: AnalyticsHelper

    @Inject
    lateinit var meetingRepository: MeetingRepository

    @Inject
    lateinit var geoLocationHelper: LocationHelper

    @Inject
    lateinit var etaDashboardNotification: EtaDashboardNotification

    private val meetingJobs: MutableMap<Long, Job> = mutableMapOf()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(
        intent: Intent,
        flags: Int,
        startId: Int,
    ): Int {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) {
            return super.onStartCommand(intent, flags, startId)
        }

        when (intent.action) {
            OPEN_ACTION -> {
                val meetingTime = intent.getLongExtra(MEETING_TIME_KEY, MEETING_TIME_DEFAULT_VALUE)
                if (meetingTime == MEETING_ID_DEFAULT_VALUE) {
                    return super.onStartCommand(intent, flags, startId)
                }

                if (!meetingJobs.contains(meetingId)) {
                    openEtaDashboard(meetingId, meetingTime)
                }
            }

            CLOSE_ACTION -> {
                closeEtaDashboard(meetingId)
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun openEtaDashboard(
        meetingId: Long,
        meetingTime: Long,
    ) {
        val notification = etaDashboardNotification.createNotification(meetingId)
        startForeground(meetingId.toInt(), notification)

        val job =
            CoroutineScope(Dispatchers.IO).launch {
                while (isInETARange(meetingTime)) {
                    upsertEtaDashboard(meetingId)
                    delay(POLLING_INTERVAL)
                }
                closeEtaDashboard(meetingId)
            }
        meetingJobs[meetingId] = job
    }

    private fun isInETARange(meetingTime: Long): Boolean {
        val nowTime = System.currentTimeMillis()
        val endTime = meetingTime + ETA_CLOSE_MINUTE
        return nowTime <= endTime
    }

    private suspend fun upsertEtaDashboard(meetingId: Long) {
        val mateEtaInfo = getLocation(meetingId) ?: return
        meetingRepository.upsertMateEta(meetingId, mateEtaInfo)
    }

    private suspend fun getLocation(meetingId: Long): MateEtaInfo? {
        return geoLocationHelper.getCurrentCoordinate().suspendFold(
            onSuccess = { location ->
                updateMatesEta(
                    meetingId,
                    isMissing = false,
                    location.latitude.toString(),
                    location.longitude.toString(),
                )
            },
            onFailure = {
                updateMatesEta(meetingId, isMissing = true)
            },
        )
    }

    private suspend fun updateMatesEta(
        meetingId: Long,
        isMissing: Boolean,
        latitude: String = DEFAULT_LATITUDE,
        longitude: String = DEFAULT_LONGITUDE,
    ): MateEtaInfo? {
        return meetingRepository.patchMatesEta(meetingId, isMissing, latitude, longitude)
            .onNetworkError { analyticsHelper.logNetworkErrorEvent(TAG, it.message) }
            .getOrNull()
    }

    private fun closeEtaDashboard(meetingId: Long) {
        val job = meetingJobs.remove(meetingId)
        job?.cancel()
        if (meetingJobs.isEmpty()) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        meetingJobs.keys.forEach(::closeEtaDashboard)
    }

    companion object {
        private const val TAG = "EtaDashboardService"
        private const val MEETING_ID_KEY = "meeting_id"
        private const val MEETING_ID_DEFAULT_VALUE = -1L

        private const val MEETING_TIME_KEY = "meeting_time"
        const val MEETING_TIME_DEFAULT_VALUE = -1L

        private const val OPEN_ACTION = "eta_dashboard_open"
        private const val CLOSE_ACTION = "eta_dashboard_close"

        private const val POLLING_INTERVAL = 10 * 1000L
        private const val ETA_CLOSE_MINUTE = 60 * 2 * 1000L

        private const val DEFAULT_LATITUDE = "0.0"
        private const val DEFAULT_LONGITUDE = "0.0"

        fun getIntent(
            context: Context,
            meetingId: Long? = null,
            meetingTime: Long? = null,
            isOpen: Boolean? = null,
        ): Intent {
            val intent = Intent(context, EtaDashboardService::class.java)
            if (meetingId != null) intent.putExtra(MEETING_ID_KEY, meetingId)
            if (meetingTime != null) intent.putExtra(MEETING_TIME_KEY, meetingTime)
            if (isOpen != null) intent.action = if (isOpen) OPEN_ACTION else CLOSE_ACTION

            return intent
        }
    }
}
