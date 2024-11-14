package com.mulberry.ody.data.local.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReinstallBroadcastReceiver : BroadcastReceiver() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    override fun onReceive(
        context: Context,
        intent: Intent,
    ) {
        val matesEtaRepository = ReinstallBroadcastReceiverEntryPoint.matesEtaRepository(context)
        coroutineScope.launch {
            matesEtaRepository.reserveAllEtaReservation()
        }
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface ReinstallBroadcastReceiverEntryPoint {
        fun matesEtaRepository(): MatesEtaRepository

        companion object {
            fun matesEtaRepository(context: Context): MatesEtaRepository {
                return EntryPointAccessors
                    .fromApplication(context, ReinstallBroadcastReceiverEntryPoint::class.java)
                    .matesEtaRepository()
            }
        }
    }
}
