package com.mulberry.ody.presentation.address.model

import com.mulberry.ody.domain.model.Address

fun Address.toAddressUiModel(): AddressUiModel = AddressUiModel(id = id, placeName = placeName, detailAddress = detailAddress)
