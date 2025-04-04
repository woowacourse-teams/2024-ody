package com.mulberry.ody.presentation.feature.meetings.model

sealed interface MeetingsUiState {
    data object Empty : MeetingsUiState

    class Meetings(val content: List<MeetingUiModel>) : MeetingsUiState
}
