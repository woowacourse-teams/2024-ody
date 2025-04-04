package com.mulberry.ody.presentation.feature.meetings.model

sealed interface MeetingsUiState {
    data object Loading : MeetingsUiState

    data object Empty : MeetingsUiState

    data object Error: MeetingsUiState

    class Meetings(val content: List<MeetingUiModel>) : MeetingsUiState
}
