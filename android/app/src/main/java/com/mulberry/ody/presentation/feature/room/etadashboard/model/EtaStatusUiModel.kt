package com.mulberry.ody.presentation.feature.room.etadashboard.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.mulberry.ody.R
import com.mulberry.ody.presentation.theme.Blue
import com.mulberry.ody.presentation.theme.Gray400
import com.mulberry.ody.presentation.theme.Green
import com.mulberry.ody.presentation.theme.Red
import com.mulberry.ody.presentation.theme.Yellow

sealed interface EtaStatusUiModel {
    val badgeColor: Color

    @get:StringRes
    val badgeMessageId: Int

    fun etaDurationMinutesMessage(
        context: Context,
        durationMinutes: Int,
    ): String {
        if (durationMinutes in ARRIVAL_SOON_VALUE_RANGE) {
            return context.getString(R.string.status_arrival_soon)
        }
        return context.getString(R.string.status_arrival_remain_time, durationMinutes)
    }

    fun canNudge(): Boolean

    fun etaStatusMessage(context: Context): String

    data object Arrived : EtaStatusUiModel {
        override val badgeColor: Color = Blue
        override val badgeMessageId: Int = R.string.badge_arrived

        override fun etaStatusMessage(context: Context): String {
            return context.getString(R.string.status_arrived)
        }

        override fun canNudge(): Boolean = false
    }

    data class ArrivalSoon(val durationMinutes: Int) : EtaStatusUiModel {
        override val badgeColor: Color = Green
        override val badgeMessageId: Int = R.string.badge_arrival_soon

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = false
    }

    data class LateWarning(val durationMinutes: Int) : EtaStatusUiModel {
        override val badgeColor: Color = Yellow
        override val badgeMessageId: Int = R.string.badge_late_warning

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = true
    }

    data class Late(val durationMinutes: Int) : EtaStatusUiModel {
        override val badgeColor: Color = Red
        override val badgeMessageId: Int = R.string.badge_late

        override fun etaStatusMessage(context: Context): String {
            return super.etaDurationMinutesMessage(context, durationMinutes)
        }

        override fun canNudge(): Boolean = true
    }

    data object Missing : EtaStatusUiModel {
        override val badgeColor: Color = Gray400
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
