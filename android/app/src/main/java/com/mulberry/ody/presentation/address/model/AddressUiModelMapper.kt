package com.mulberry.ody.presentation.address.model

import com.mulberry.ody.domain.model.Location

fun List<Location>.toAddressUiModels(): List<AddressUiModel> = map { it.toAddressUiModel() }

private fun Location.toAddressUiModel(): AddressUiModel = AddressUiModel(id = id, name = name, address = address)
