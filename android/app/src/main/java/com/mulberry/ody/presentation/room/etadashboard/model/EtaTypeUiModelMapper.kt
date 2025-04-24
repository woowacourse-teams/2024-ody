package com.mulberry.ody.presentation.room.etadashboard.model

import com.mulberry.ody.domain.model.EtaStatus

fun EtaStatus.toEtaStatusUiModel(): EtaStatusUiModel {
    return when (this) {
        EtaStatus.Arrived -> EtaStatusUiModel.Arrived
        is EtaStatus.ArrivalSoon -> EtaStatusUiModel.ArrivalSoon(durationMinutes)
        is EtaStatus.LateWarning -> EtaStatusUiModel.LateWarning(durationMinutes)
        is EtaStatus.Late -> EtaStatusUiModel.Late(durationMinutes)
        EtaStatus.Missing -> EtaStatusUiModel.Missing
    }
}
