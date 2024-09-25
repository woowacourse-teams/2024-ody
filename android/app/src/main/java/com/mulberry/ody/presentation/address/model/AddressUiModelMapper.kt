package com.mulberry.ody.presentation.address.model

import com.mulberry.ody.domain.model.Address

fun List<Address>.toAddressUiModels(): List<AddressUiModel> = map { it.toAddressUiModel() }

private fun Address.toAddressUiModel(): AddressUiModel = AddressUiModel(id = id, name = name, address = roadNameAddress)
