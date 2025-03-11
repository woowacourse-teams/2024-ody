package com.mulberry.ody.presentation.feature.room.detail.model

import com.mulberry.ody.domain.model.DetailMeeting
import com.mulberry.ody.domain.model.Mate

fun DetailMeeting.toMateUiModels(): List<MateUiModel> = mates.map { it.toMateUiModel() }

private fun Mate.toMateUiModel(): MateUiModel = MateUiModel(nickname, imageUrl)
