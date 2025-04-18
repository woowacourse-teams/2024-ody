package com.mulberry.ody.presentation.feature.address

import androidx.paging.map
import com.mulberry.ody.address
import com.mulberry.ody.fake.FakeAddressRepository
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
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
            AddressSearchViewModel(addressRepository = FakeAddressRepository)
    }

    @Test
    fun `주소에 대한 위경도를 받아온다`() {
        runTest {
            // given
            val addressSearchKeyword = "사당역"

            // when
            viewModel.searchAddress(addressSearchKeyword)

            // then
            viewModel.address.value.map { actual ->
                assertThat(actual).isEqualTo(address)
            }
        }
    }
}
