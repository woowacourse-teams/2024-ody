package com.mulberry.ody.presentation.meetings

import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.meetings
import com.mulberry.ody.presentation.meetings.model.toMeetingUiModels
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import com.mulberry.ody.util.valueOnAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
        runTest {
            // when
            viewModel.fetchMeetings()

            // then
            val actual = viewModel.meetings.first()
            val expected = meetings.toMeetingUiModels()
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `약속 모임들 중 하나를 선택하면 약속 로그 화면으로 이동한다`() {
        runTest {
            // given
            val meetingId = 1L

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.navigateToNotificationLog(meetingId)
                }

            // then
            val expected = MeetingsNavigateAction.NavigateToNotificationLog(meetingId)
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `약속 모임 리스트 화면에서 친구 위치 현황 화면으로 이동한다`() {
        runTest {
            // given
            val meetingId = 1L

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.navigateToEtaDashboard(meetingId)
                }

            // then
            val expected = MeetingsNavigateAction.NavigateToEtaDashboard(meetingId)
            assertThat(actual).isEqualTo(expected)
        }
    }
}
