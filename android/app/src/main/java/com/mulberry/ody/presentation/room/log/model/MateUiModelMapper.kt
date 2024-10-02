package com.mulberry.ody.presentation.room.log.model

import com.mulberry.ody.domain.model.Mate
import com.mulberry.ody.domain.model.Meeting

fun Meeting.toMateUiModels(): List<MateUiModel> = mates.map { it.toMateUiModel() }

private fun Mate.toMateUiModel(): MateUiModel = MateUiModel(nickname, imageUrl)
