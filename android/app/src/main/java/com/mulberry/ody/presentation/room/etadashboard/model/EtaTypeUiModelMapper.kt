package com.mulberry.ody.presentation.room.etadashboard.model

import com.mulberry.ody.domain.model.EtaType

fun EtaType.toEtaTypeUiModel(): EtaTypeUiModel {
    return when (this) {
        EtaType.LATE_WARNING -> EtaTypeUiModel.LATE_WARNING
        EtaType.ARRIVAL_SOON -> EtaTypeUiModel.ARRIVAL_SOON
        EtaType.ARRIVED -> EtaTypeUiModel.ARRIVED
        EtaType.LATE -> EtaTypeUiModel.LATE
        EtaType.MISSING -> EtaTypeUiModel.MISSING
    }
}
