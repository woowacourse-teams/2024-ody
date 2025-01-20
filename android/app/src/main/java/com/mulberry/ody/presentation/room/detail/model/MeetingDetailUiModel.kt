package com.mulberry.ody.presentation.room.detail.model

data class MeetingDetailUiModel(
    val id: Long,
    val name: String,
    val dateTime: String,
    val destinationAddress: String,
    val departureAddress: String,
    val departureTime: String,
    val routeTime: String,
    val mates: List<String>,
    val inviteCode: String,
    val isEtaAccessible: Boolean,
) {
    fun isDefault(): Boolean = this == DEFAULT

    companion object {
        val DEFAULT: MeetingDetailUiModel =
            MeetingDetailUiModel(
                id = -1L,
                name = "-",
                dateTime = "0",
                destinationAddress = "-",
                departureAddress = "-",
                departureTime = "-",
                routeTime = "-",
                mates = listOf("-"),
                inviteCode = "",
                isEtaAccessible = false,
            )
    }
}
