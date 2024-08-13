package com.woowacourse.ody.viewmodel

import com.woowacourse.ody.fake.FakeAnalyticsHelper
import com.woowacourse.ody.fake.FakeGeoLocationRepository
import com.woowacourse.ody.presentation.address.AddressSearchViewModel
import com.woowacourse.ody.util.CoroutinesTestExtension
import com.woowacourse.ody.util.InstantTaskExecutorExtension
import com.woowacourse.ody.util.getOrAwaitValue
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
                locationRepository = FakeGeoLocationRepository,
            )
    }

    @Test
    fun `주소에 대한 위경도를 받아온다`() {
        // given
        val address = "인천광역시 남동구"

        // when
        viewModel.fetchGeoLocation(address)

        // then
        val actual = viewModel.geoLocation.getOrAwaitValue()
        assertThat(actual.address).isEqualTo(address)
    }
}
