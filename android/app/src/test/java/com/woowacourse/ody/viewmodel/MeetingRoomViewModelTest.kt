package com.woowacourse.ody.viewmodel

import com.woowacourse.ody.domain.repository.ody.MatesEtaRepository
import com.woowacourse.ody.fake.FakeAnalyticsHelper
import com.woowacourse.ody.fake.FakeMatesEtaRepository
import com.woowacourse.ody.fake.FakeMeetingRepository
import com.woowacourse.ody.fake.FakeNotificationLogRepository
import com.woowacourse.ody.presentation.room.MeetingRoomViewModel
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
class MeetingRoomViewModelTest {
    private lateinit var viewModel: MeetingRoomViewModel
    private lateinit var matesEtaRepository: MatesEtaRepository

    @BeforeEach
    fun setUp() {
        matesEtaRepository = FakeMatesEtaRepository
        viewModel =
            MeetingRoomViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                meetingId = 1L,
                matesEtaRepository = FakeMatesEtaRepository,
                notificationLogRepository = FakeNotificationLogRepository,
                meetingRepository = FakeMeetingRepository,
            )
        matesEtaRepository.fetchMatesEta(1L)
    }

    @Test
    fun `친구들과 나의 위치 현황을 볼 수 있다`() {
        // when
        val etaType =
            viewModel.mateEtaUiModels.getOrAwaitValue()?.map {
                it.getEtaDurationMinuteTypeUiModel()
            }

        // then
        assertThat(etaType).isEqualTo(
            listOf(
                EtaDurationMinuteTypeUiModel.ARRIVAL_REMAIN_TIME,
                EtaDurationMinuteTypeUiModel.ARRIVAL_SOON,
                EtaDurationMinuteTypeUiModel.ARRIVED,
                EtaDurationMinuteTypeUiModel.MISSING,
            ),
        )
    }

    @Test
    fun `친구들과 나의 남은 시간을 볼 수 있다`() {
        // when
        val durationMinute =
            viewModel.mateEtaUiModels.getOrAwaitValue()?.map {
                it.durationMinute
            }

        // then
        assertThat(durationMinute).isEqualTo(
            listOf(
                83,
                10,
                0,
                -1,
            ),
        )
    }
}
