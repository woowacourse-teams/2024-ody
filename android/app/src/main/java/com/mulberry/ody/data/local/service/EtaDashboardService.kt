package com.mulberry.ody.data.local.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mulberry.ody.domain.apiresult.getOrNull
import com.mulberry.ody.domain.apiresult.onNetworkError
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
import kotlinx.coroutines.isActive
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
                val notification = etaDashboardNotification.createNotification(meetingId)
                startForeground(meetingId.toInt(), notification)
                openEtaDashboard(meetingId)
            }

            CLOSE_ACTION -> {
                closeEtaDashboard(meetingId)
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun openEtaDashboard(meetingId: Long) {
        meetingJobs[meetingId]?.cancel()

        val job =
            CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    upsertEtaDashboard(meetingId)
                    delay(POLLING_INTERVAL)
                }
            }

        meetingJobs[meetingId] = job
    }

    private suspend fun upsertEtaDashboard(meetingId: Long) {
        val mateEtaInfo = getLocation(meetingId) ?: return
        meetingRepository.upsertMateEta(meetingId, mateEtaInfo)
    }

    private suspend fun getLocation(meetingId: Long): MateEtaInfo? {
        return geoLocationHelper.getCurrentCoordinate().fold(
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
        val job = meetingJobs.remove(meetingId) ?: return
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        meetingJobs.keys.forEach(::closeEtaDashboard)
    }

    companion object {
        private const val TAG = "EtaDashboardService"
        const val MEETING_ID_KEY = "meeting_id"
        const val MEETING_ID_DEFAULT_VALUE = -1L

        private const val OPEN_ACTION = "eta_dashboard_open"
        private const val CLOSE_ACTION = "eta_dashboard_close"

        private const val POLLING_INTERVAL = 10 * 1000L

        private const val DEFAULT_LATITUDE = "0.0"
        private const val DEFAULT_LONGITUDE = "0.0"

        fun getIntent(
            context: Context,
            meetingId: Long,
            isOpen: Boolean = true,
        ): Intent {
            return Intent(context, EtaDashboardService::class.java).apply {
                putExtra(MEETING_ID_KEY, meetingId)
                action = if (isOpen) OPEN_ACTION else CLOSE_ACTION
            }
        }
    }
}
