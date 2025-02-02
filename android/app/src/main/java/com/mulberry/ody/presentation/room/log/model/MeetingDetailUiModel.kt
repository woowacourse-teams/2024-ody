package com.mulberry.ody.presentation.room.log.model

data class MeetingDetailUiModel(
    val id: Long,
    val name: String,
    val targetPosition: String,
    val meetingTime: String,
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
                targetPosition = "-",
                meetingTime = "-",
                mates = listOf("-"),
                inviteCode = "",
                isEtaAccessible = false,
            )
    }
}
