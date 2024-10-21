package com.mulberry.ody.presentation.room.etadashboard.model

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.mulberry.ody.R

sealed class EtaStatusUiModel {
    @get:ColorRes
    abstract val badgeColorId: Int

    @get:StringRes
    abstract val badgeMessageId: Int

    protected fun etaDurationMinutesMessage(context: Context, durationMinutes: Int): String {
        if (durationMinutes in ARRIVAL_SOON_VALUE_RANGE) {
            return context.getString(R.string.status_arrival_soon)
        }
        return context.getString(R.string.status_arrival_remain_time, durationMinutes)
    }

    abstract fun canNudge(): Boolean

    abstract fun etaStatusMessage(context: Context): String

    data object Arrived : EtaStatusUiModel() {
        override val badgeColorId: Int = R.color.blue
        override val badgeMessageId: Int = R.string.badge_arrived

        override fun etaStatusMessage(context: Context): String {
            return context.getString(R.string.status_arrived)
        }

        override fun canNudge(): Boolean = false
    }

    data class ArrivalSoon(val durationMinutes: Int) : EtaStatusUiModel() {
        override val badgeColorId: Int = R.color.green
        override val badgeMessageId: Int = R.string.badge_arrival_soon

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = false
    }

    data class LateWarning(val durationMinutes: Int) : EtaStatusUiModel() {
        override val badgeColorId: Int = R.color.yellow
        override val badgeMessageId: Int = R.string.badge_late_warning

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = true
    }

    data class Late(val durationMinutes: Int) : EtaStatusUiModel() {
        override val badgeColorId: Int = R.color.red
        override val badgeMessageId: Int = R.string.badge_late

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = true
    }

    data object Missing : EtaStatusUiModel() {
        override val badgeColorId: Int = R.color.gray_400
        override val badgeMessageId: Int = R.string.badge_missing

        override fun etaStatusMessage(context: Context): String {
            return context.getString(R.string.status_missing)
        }

        override fun canNudge(): Boolean = false
    }

    companion object {
        private val ARRIVAL_SOON_VALUE_RANGE = 1..10
    }
}
