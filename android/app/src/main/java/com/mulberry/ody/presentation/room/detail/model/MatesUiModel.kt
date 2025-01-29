package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.presentation.room.detail.model.MatesUiModel.Companion.INVITE_CODE_COPY_VIEW_TYPE
import com.mulberry.ody.presentation.room.detail.model.MatesUiModel.Companion.MATE_VIEW_TYPE

sealed interface MatesUiModel {
    val viewType: Int

    companion object {
        const val MATE_VIEW_TYPE = 0
        const val INVITE_CODE_COPY_VIEW_TYPE = 1
    }
}

class MateUiModel(
    val nickname: String,
    val imageUrl: String,
    override val viewType: Int = MATE_VIEW_TYPE,
) : MatesUiModel

class InviteCodeCopyUiModel(
    override val viewType: Int = INVITE_CODE_COPY_VIEW_TYPE,
) : MatesUiModel
