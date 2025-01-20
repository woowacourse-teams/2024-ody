package com.mulberry.ody.presentation.room.detail.model

data class DetailMeetingUiModel(
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
        val DEFAULT: DetailMeetingUiModel =
            DetailMeetingUiModel(
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
