package com.mulberry.ody.presentation.feature.meetings.model

sealed interface MeetingsUiState {
    class Meetings(val content: List<MeetingUiModel>) : MeetingsUiState

    data object Empty : MeetingsUiState
}
