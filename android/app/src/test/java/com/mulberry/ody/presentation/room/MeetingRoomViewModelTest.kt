package com.mulberry.ody.presentation.room

import com.mulberry.ody.domain.repository.ody.MatesEtaRepository
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeImageShareHelper
import com.mulberry.ody.fake.FakeImageStorage
import com.mulberry.ody.fake.FakeMatesEtaRepository
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.fake.FakeNotificationLogRepository
import com.mulberry.ody.mateEtaInfo
import com.mulberry.ody.meeting
import com.mulberry.ody.meetingId
import com.mulberry.ody.notificationLogs
import com.mulberry.ody.presentation.room.etadashboard.model.toMateEtaUiModels
import com.mulberry.ody.presentation.room.log.model.toMeetingUiModel
import com.mulberry.ody.presentation.room.log.model.toNotificationUiModels
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
class MeetingRoomViewModelTest {
    private lateinit var viewModel: MeetingRoomViewModel
    private lateinit var matesEtaRepository: MatesEtaRepository

    @BeforeEach
    fun setUp() {
        matesEtaRepository = FakeMatesEtaRepository
        viewModel =
            MeetingRoomViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                meetingId = meetingId,
                matesEtaRepository = matesEtaRepository,
                notificationLogRepository = FakeNotificationLogRepository,
                meetingRepository = FakeMeetingRepository,
                imageStorage = FakeImageStorage,
                imageShareHelper = FakeImageShareHelper,
            )
    }

    @Test
    fun `친구들과 나의 위치 현황을 볼 수 있다`() {
        runTest {
            // when
            val actual = viewModel.mateEtaUiModels.first()

            // then
            val expected = mateEtaInfo.toMateEtaUiModels()
            assertThat(actual).isNotNull
            assertThat(actual).isEqualTo(expected)
        }
    }

    @Test
    fun `약속 id에 맞는 약속을 조회하고 해당하는 로그 목록을 가져온다`() {
        runTest {
            // then
            val meetingUiModel = viewModel.meeting.first()
            assertThat(meetingUiModel).isEqualTo(meeting.toMeetingUiModel())

            val notificationLogUiModel = viewModel.notificationLogs.first()
            assertThat(notificationLogUiModel).isEqualTo(notificationLogs.toNotificationUiModels())
        }
    }

    @Test
    fun `친구 재촉을 하면 친구 재촉이 성공한다`() {
        runTest {
            // when
            val actual =
                viewModel.nudgeSuccessMate.valueOnAction {
                    viewModel.nudgeMate(1, 0)
                }

            // then
            assertThat(actual).isEqualTo("콜리")
        }
    }
}
