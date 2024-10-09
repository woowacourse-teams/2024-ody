package com.mulberry.ody.data.local.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.mulberry.ody.R
import com.mulberry.ody.data.local.db.MateEtaInfoDao
import com.mulberry.ody.data.local.entity.eta.MateEtaInfoEntity
import com.mulberry.ody.domain.model.MateEtaInfo
import com.mulberry.ody.domain.repository.ody.MeetingRepository
import com.mulberry.ody.presentation.common.analytics.AnalyticsHelper
import com.mulberry.ody.presentation.common.analytics.logNetworkErrorEvent
import com.mulberry.ody.presentation.common.gps.LocationHelper
import com.mulberry.ody.presentation.meetings.MeetingsActivity
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
    lateinit var mateEtaInfoDao: MateEtaInfoDao

    @Inject
    lateinit var geoLocationHelper: LocationHelper

    private val meetingJobs: MutableMap<Long, Job> = mutableMapOf()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e("TEST", "onStartCommand")
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) return super.onStartCommand(
            intent,
            flags,
            startId
        )

        when (intent.action) {
            OPEN -> {
                createNotificationChannel()
                startForeground(meetingId.toInt(), createNotification(meetingId))
                openEtaDashboard(meetingId)
            }

            CLOSE -> {
                Log.e("TEST", "onStartCommand CLOSE")
                closeEtaDashboard(meetingId)
            }
        }
        return START_REDELIVER_INTENT
    }

    private fun createNotification(meetingId: Long): Notification {
        val intent = Intent(this, MeetingsActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, meetingId.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("친구에게 도착 정보를 공유하기 위해 위치를 수집하고 있어요")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(1, 1000))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun openEtaDashboard(meetingId: Long) {
        meetingJobs[meetingId]?.cancel()

        var count = 1
        val job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                Log.e("TEST", "upsertEtaDashboard meetingId: $meetingId, ${count++}")
                upsertEtaDashboard(meetingId)
                delay(INTERVAL)
            }
        }

        meetingJobs[meetingId] = job
    }

    private suspend fun upsertEtaDashboard(meetingId: Long) {
        val mateEtaInfo = getLocation(meetingId)
        if (mateEtaInfo != null) {
            val mateEtaInfoEntity =
                MateEtaInfoEntity(meetingId, mateEtaInfo.userId, mateEtaInfo.mateEtas)
            mateEtaInfoDao.upsert(mateEtaInfoEntity)
        }
    }

    private suspend fun getLocation(meetingId: Long): MateEtaInfo? {
        return geoLocationHelper.getCurrentCoordinate().fold(
            onSuccess = { location ->
                updateMatesEta(
                    meetingId,
                    isMissing = false,
                    location.latitude.toString(),
                    location.longitude.toString()
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
            .onFailure { analyticsHelper.logNetworkErrorEvent(TAG, it.message) }
            .getOrNull()
    }

    private fun closeEtaDashboard(meetingId: Long) {
        val job = meetingJobs.remove(meetingId) ?: return
        job.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("TEST", "service onDestroy")
        meetingJobs.keys.forEach(::closeEtaDashboard)
    }

    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(
                CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_HIGH,
            )
        channel.description = "NOTIFICATION_DESCRIPTION"

        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "EtaDashboardService"
        const val MEETING_ID_KEY = "meeting_id"
        const val MEETING_ID_DEFAULT_VALUE = -1L

        const val OPEN = "eta_dashboard_open"
        const val CLOSE = "eta_dashboard_close"

        private const val DEFAULT_LATITUDE = "0.0"
        private const val DEFAULT_LONGITUDE = "0.0"

        private const val INTERVAL = 10 * 1000L
        private const val CHANNEL_ID = "channel"
    }
}
