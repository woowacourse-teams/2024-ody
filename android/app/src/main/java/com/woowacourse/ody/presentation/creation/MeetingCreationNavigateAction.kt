package com.woowacourse.ody.presentation.creation

sealed interface MeetingCreationNavigateAction {
    data object NavigateToHome : MeetingCreationNavigateAction

    data object NavigateToCreationComplete : MeetingCreationNavigateAction
}
