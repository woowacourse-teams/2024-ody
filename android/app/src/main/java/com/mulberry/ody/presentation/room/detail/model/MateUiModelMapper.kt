package com.mulberry.ody.presentation.room.detail.model

import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.DetailMeeting

fun DetailMeeting.toMateUiModels(): List<MateUiModel> = mates.map { it.toMateUiModel() }

private fun Mate.toMateUiModel(): MateUiModel = MateUiModel(nickname, imageUrl)
