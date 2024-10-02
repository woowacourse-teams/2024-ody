package com.mulberry.ody.presentation.address

import com.mulberry.ody.addresses
import com.mulberry.ody.fake.FakeAddressRepository
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.presentation.address.model.toAddressUiModels
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import com.mulberry.ody.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class AddressSearchViewModelTest {
    private lateinit var viewModel: AddressSearchViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            AddressSearchViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                addressRepository = FakeAddressRepository,
            )
    }

    @Test
    fun `주소에 대한 위경도를 받아온다`() {
        // given
        viewModel.addressSearchKeyword.value = "사당역"

        // when
        viewModel.searchAddress()

        // then
        val actual = viewModel.addressUiModels.getOrAwaitValue()
        assertThat(actual).isEqualTo(addresses.toAddressUiModels())
    }
}
