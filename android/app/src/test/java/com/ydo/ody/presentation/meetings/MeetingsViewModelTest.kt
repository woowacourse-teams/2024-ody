package com.ydo.ody.presentation.meetings

import com.ydo.ody.fake.FakeAnalyticsHelper
import com.ydo.ody.fake.FakeMeetingRepository
import com.ydo.ody.meetingCatalogs
import com.ydo.ody.presentation.meetings.model.toMeetingCatalogUiModel
import com.ydo.ody.util.CoroutinesTestExtension
import com.ydo.ody.util.InstantTaskExecutorExtension
import com.ydo.ody.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class MeetingsViewModelTest {
    private val analyticsHelper = FakeAnalyticsHelper
    private val meetingRepository = FakeMeetingRepository
    private lateinit var viewModel: MeetingsViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingsViewModel(
                analyticsHelper,
                meetingRepository,
            )
    }

    @Test
    fun `약속 모임 리스트를 가져온다`() {
        // when
        viewModel.fetchMeetingCatalogs()

        // then
        val actual = viewModel.meetingCatalogs.getOrAwaitValue()
        val expected = meetingCatalogs.map { it.toMeetingCatalogUiModel() }
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `약속 모임들 중 하나를 선택하면 약속 로그 화면으로 이동한다`() {
        // given
        val meetingId = 1L

        // when
        viewModel.navigateToNotificationLog(meetingId)

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        val expected = MeetingsNavigateAction.NavigateToNotificationLog(meetingId)
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `약속 모임 리스트 화면에서 친구 위치 현황 화면으로 이동한다`() {
        // given
        val meetingId = 1L

        // when
        viewModel.navigateToEtaDashboard(meetingId)

        // then
        val actual = viewModel.navigateAction.getOrAwaitValue()
        val expected = MeetingsNavigateAction.NavigateToEtaDashboard(meetingId)
        assertThat(actual).isEqualTo(expected)
    }
}
