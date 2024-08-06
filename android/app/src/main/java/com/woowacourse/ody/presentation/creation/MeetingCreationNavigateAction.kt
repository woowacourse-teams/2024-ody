package com.woowacourse.ody.presentation.creation

sealed interface MeetingCreationNavigateAction {
    data object NavigateToIntro : MeetingCreationNavigateAction

    data object NavigateToCreationComplete : MeetingCreationNavigateAction
}
