package com.mulberry.ody.presentation.room.etadashboard.model

data class MateEtaUiModel(
    val nickname: String,
    val etaTypeUiModel: EtaTypeUiModel,
    val durationMinute: Int,
    val isMissing: Boolean,
    val isUserSelf: Boolean,
    val userId: Long,
    val mateId: Long,
) {
    fun getEtaDurationMinuteTypeUiModel(): EtaDurationMinuteTypeUiModel {
        return when (durationMinute) {
            ARRIVED_VALUE -> EtaDurationMinuteTypeUiModel.ARRIVED
            MISSING_VALUE -> EtaDurationMinuteTypeUiModel.MISSING
            in ARRIVAL_SOON_VALUE_RANGE -> EtaDurationMinuteTypeUiModel.ARRIVAL_SOON
            else -> EtaDurationMinuteTypeUiModel.ARRIVAL_REMAIN_TIME
        }
    }

    companion object {
        const val ARRIVED_VALUE = 0
        const val MISSING_VALUE = -1
        val ARRIVAL_SOON_VALUE_RANGE = 1..10
    }
}
