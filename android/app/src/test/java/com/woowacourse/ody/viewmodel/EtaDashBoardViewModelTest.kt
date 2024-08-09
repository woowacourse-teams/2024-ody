package com.woowacourse.ody.viewmodel

import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.fake.FakeMatesEtaRepository
import com.woowacourse.ody.presentation.room.etadashboard.EtaDashboardViewModel
import com.woowacourse.ody.presentation.room.etadashboard.model.EtaDurationMinuteTypeUiModel
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
class EtaDashBoardViewModelTest {
    private lateinit var viewModel: EtaDashboardViewModel
    private lateinit var repository: MatesEtaRepository

    @BeforeEach
    fun setUp() {
        repository = FakeMatesEtaRepository
        viewModel = EtaDashboardViewModel(1L, repository)
        repository.fetchMatesEta(1L)
    }


    @Test
    fun `친구들과 나의 위치 현황을 볼 수 있다`() {
        val etaType = viewModel.mateEtaUiModels.getOrAwaitValue()?.map {
            it.getEtaDurationMinuteTypeUiModel()
        }

        assertThat(etaType).isEqualTo(
            listOf(
                EtaDurationMinuteTypeUiModel.ARRIVAL_REMAIN_TIME,
                EtaDurationMinuteTypeUiModel.ARRIVAL_SOON,
                EtaDurationMinuteTypeUiModel.ARRIVED,
                EtaDurationMinuteTypeUiModel.MISSING,
            )
        )
    }

    @Test
    fun `친구들과 나의 남은 시간을 볼 수 있다`() {
        val durationMinute =
            viewModel.mateEtaUiModels.getOrAwaitValue()?.map {
                it.durationMinute
            }

        assertThat(durationMinute).isEqualTo(
            listOf(
                83,
                10,
                0,
                -1,
            )
        )
    }
}

