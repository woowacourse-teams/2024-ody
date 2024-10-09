package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.WorkManager
import com.mulberry.ody.data.local.service.AlarmManagerHelper.Companion.MEETING_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.AlarmManagerHelper.Companion.MEETING_ID_KEY
import com.mulberry.ody.data.local.service.AlarmManagerHelper.Companion.RESERVE_COUNT_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.AlarmManagerHelper.Companion.RESERVE_COUNT_KEY
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EtaDashboardBroadcastReceiver : BroadcastReceiver() {
    @Inject lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        val reserveCount = intent.getIntExtra(RESERVE_COUNT_KEY, RESERVE_COUNT_DEFAULT_VALUE)

        if (meetingId == MEETING_ID_DEFAULT_VALUE) return
        if (reserveCount == RESERVE_COUNT_DEFAULT_VALUE) return

        Log.e("TEST", "onReceive() meetingId $meetingId $reserveCount")
        val etaDashboardWorker = EtaDashboardWorker.getWorkRequest(meetingId, reserveCount)
        workManager.enqueue(etaDashboardWorker)
    }
}
