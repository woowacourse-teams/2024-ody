package com.mulberry.ody.presentation.feature.meetings

import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.meetingUiModel
import com.mulberry.ody.meetings
import com.mulberry.ody.presentation.feature.meetings.model.MeetingsUiState
import com.mulberry.ody.presentation.feature.meetings.model.toMeetingUiModels
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import com.mulberry.ody.util.valueOnAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

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
            val actual = (viewModel.meetingsUiState.value as MeetingsUiState.Meetings).content
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
            val meeting = meetingUiModel.copy(dateTime = LocalDateTime.now().plusMinutes(20))

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.navigateToEta(meeting)
                }

            // then
            val expected = MeetingsNavigateAction.NavigateToEtaDashboard(meeting.id)
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `약속 시간이 1시간 후인 경우 친구 위치 현황 화면으로 이동하지 않는다`() {
        runTest {
            // given
            val meeting = meetingUiModel.copy(dateTime = LocalDateTime.now().plusMinutes(60))

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.navigateToEta(meeting)
                }

            // then
            assertThat(actual).isNull()
        }
    }
}
