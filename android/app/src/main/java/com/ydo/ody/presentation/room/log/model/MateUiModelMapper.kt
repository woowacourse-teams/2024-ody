package com.ydo.ody.presentation.room.log.model

import com.ydo.ody.domain.model.Mate
import com.ydo.ody.domain.model.Meeting

fun Meeting.toMateUiModels(): List<MateUiModel> = mates.map { it.toMateUiModel() }

private fun Mate.toMateUiModel(): MateUiModel = MateUiModel(nickname, imageUrl)
