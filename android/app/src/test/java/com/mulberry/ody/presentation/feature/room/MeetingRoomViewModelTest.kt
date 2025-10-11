package com.mulberry.ody.presentation.feature.room

import com.mulberry.ody.detailMeeting
import com.mulberry.ody.domain.usecase.CloseEtaDashboardUseCase
import com.mulberry.ody.domain.usecase.ExitMeetingUseCase
import com.mulberry.ody.domain.usecase.GetDetailMeetingUseCase
import com.mulberry.ody.domain.usecase.GetMatesEtaInfoUseCase
import com.mulberry.ody.domain.usecase.GetNotificationLogsUseCase
import com.mulberry.ody.domain.usecase.IsFirstSeenEtaDashboardUseCase
import com.mulberry.ody.domain.usecase.NudgeMateUseCase
import com.mulberry.ody.domain.usecase.UpdateEtaDashboardSeenUseCase
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeImageShareHelper
import com.mulberry.ody.fake.FakeImageStorage
import com.mulberry.ody.fake.FakeMatesEtaRepository
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.fake.FakeNotificationLogRepository
import com.mulberry.ody.mateEtaInfo
import com.mulberry.ody.meetingId
import com.mulberry.ody.notificationLogs
import com.mulberry.ody.presentation.feature.room.detail.model.toDetailMeetingUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.EtaStatusUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.MateEtaUiModel
import com.mulberry.ody.presentation.feature.room.etadashboard.model.toMateEtaUiModels
import com.mulberry.ody.presentation.feature.room.log.model.toNotificationLogUiModels
import com.mulberry.ody.util.CoroutinesTestExtension
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
class MeetingRoomViewModelTest {
    private lateinit var viewModel: MeetingRoomViewModel

    @BeforeEach
    fun setUp() {
        val getMatesEtaInfoUseCase = GetMatesEtaInfoUseCase(FakeMatesEtaRepository)
        val isFirstSeenEtaDashboardUseCase = IsFirstSeenEtaDashboardUseCase(FakeMatesEtaRepository)
        val closeEtaDashboardUseCase = CloseEtaDashboardUseCase(FakeMatesEtaRepository)
        val updateEtaDashboardSeenUseCase = UpdateEtaDashboardSeenUseCase(FakeMatesEtaRepository)
        val getNotificationLogsUseCase = GetNotificationLogsUseCase(FakeNotificationLogRepository)
        val getDetailMeetingUseCase = GetDetailMeetingUseCase(FakeMeetingRepository)
        val nudgeMateUseCase = NudgeMateUseCase(FakeMeetingRepository)
        val exitMeetingUseCase = ExitMeetingUseCase(FakeMeetingRepository)

        viewModel =
            MeetingRoomViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                meetingId = meetingId,
                getMatesEtaInfoUseCase = getMatesEtaInfoUseCase,
                isFirstSeenEtaDashboardUseCase = isFirstSeenEtaDashboardUseCase,
                closeEtaDashboardUseCase = closeEtaDashboardUseCase,
                updateEtaDashboardSeenUseCase = updateEtaDashboardSeenUseCase,
                getNotificationLogsUseCase = getNotificationLogsUseCase,
                getDetailMeetingUseCase = getDetailMeetingUseCase,
                nudgeMateUseCase = nudgeMateUseCase,
                exitMeetingUseCase = exitMeetingUseCase,
                imageStorage = FakeImageStorage,
                imageShareHelper = FakeImageShareHelper,
            )
    }

    @Test
    fun `친구들과 나의 위치 현황을 볼 수 있다`() {
        runTest {
            // when
            val actual = viewModel.mateEtas.first()

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
            assertThat(meetingUiModel).isEqualTo(detailMeeting.toDetailMeetingUiModel())

            val notificationLogUiModel = viewModel.notificationLogs.first()
            assertThat(notificationLogUiModel).isEqualTo(notificationLogs.toNotificationLogUiModels())
        }
    }

    @Test
    fun `친구 재촉을 하면 친구 재촉이 성공한다`() {
        runTest {
            // given
            val mateEta = MateEtaUiModel(
                nickname = "콜리",
                status = EtaStatusUiModel.Late(3),
                userId = 1L,
                mateId = 0L,
            )

            // when
            viewModel.mateEtas.first()
            val actual = viewModel.nudgeSuccessMate.valueOnAction { viewModel.nudgeMate(mateEta) }

            // then
            assertThat(actual).isEqualTo("콜리")
        }
    }
}
