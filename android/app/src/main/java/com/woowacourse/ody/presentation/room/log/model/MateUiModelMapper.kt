package com.woowacourse.ody.presentation.room.log.model

import com.woowacourse.ody.domain.model.Mate
import com.woowacourse.ody.domain.model.Meeting

fun Meeting.toMateUiModels(): List<MateUiModel> = mates.map { it.toMateUiModel() }

private fun Mate.toMateUiModel(): MateUiModel = MateUiModel(nickname, imageUrl)
