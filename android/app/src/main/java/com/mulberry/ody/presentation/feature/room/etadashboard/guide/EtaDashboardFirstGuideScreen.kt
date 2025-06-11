package com.mulberry.ody.presentation.feature.room.etadashboard.guide

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mulberry.ody.R
import com.mulberry.ody.presentation.feature.room.etadashboard.guide.model.EtaDashboardGuideUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.theme.OdyTheme

@Composable
fun EtaDashboardFirstGuideScreen(onClick: () -> Unit) {
    val mateEtas = listOf(
        MateEtaUiModel(
            "올리브",
            EtaStatusUiModel.ArrivalSoon(10),
            userId = 1L,
            mateId = 1L,
        ),
        MateEtaUiModel(
            "콜리",
            EtaStatusUiModel.LateWarning(28),
            userId = 2L,
            mateId = 2L,
        ),
        MateEtaUiModel(
            "해음",
            EtaStatusUiModel.Arrived,
            userId = 3L,
            mateId = 3L,
        ),
        MateEtaUiModel(
            "제리",
            EtaStatusUiModel.Arrived,
            userId = 4L,
            mateId = 4L,
        ),
    )

    EtaDashboardGuideLayout(
        guideUiModel = EtaDashboardGuideUiModel(
            mateEtas = mateEtas,
            nudgeMessageId = R.string.eta_dashboard_guide_late_warning_nudge,
            messageId = R.string.eta_dashboard_guide_before_meeting_time,
            buttonMessageId = R.string.next_button,
            buttonImageId = R.drawable.ic_arrow_right,
        ),
        onClick = onClick,
    )
}

@Composable
@Preview(showSystemUi = true)
private fun EtaDashboardFirstGuideScreenPreview() {
    OdyTheme {
        EtaDashboardFirstGuideScreen(onClick = { })
    }
}
