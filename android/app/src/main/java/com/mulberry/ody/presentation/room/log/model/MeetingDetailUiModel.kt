package com.mulberry.ody.presentation.room.log.model

data class MeetingDetailUiModel(
    val id: Long = ID_DEFAULT_VALUE,
    val name: String = "-",
    val targetPosition: String = "-",
    val meetingTime: String = "-",
    val mates: List<String> = listOf("-"),
    val inviteCode: String = "-",
    val isEtaAccessible: Boolean = false,
) {
    fun isDefault(): Boolean = id == ID_DEFAULT_VALUE

    companion object {
        private const val ID_DEFAULT_VALUE = -1L
    }
}
