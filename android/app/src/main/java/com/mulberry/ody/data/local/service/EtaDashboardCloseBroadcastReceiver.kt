package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.CLOSE
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_DEFAULT_VALUE
import com.mulberry.ody.data.local.service.EtaDashboardService.Companion.MEETING_ID_KEY

class EtaDashboardCloseBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val meetingId = intent.getLongExtra(MEETING_ID_KEY, MEETING_ID_DEFAULT_VALUE)
        if (meetingId == MEETING_ID_DEFAULT_VALUE) return

        val serviceIntent = Intent(context, EtaDashboardService::class.java).apply {
            putExtra(MEETING_ID_KEY, meetingId)
            action = CLOSE
        }
        context.stopService(serviceIntent)
    }
}
