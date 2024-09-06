package com.ydo.ody.presentation.join

import com.ydo.ody.domain.model.GeoLocation
import com.ydo.ody.fake.FakeAnalyticsHelper
import com.ydo.ody.fake.FakeJoinRepository
import com.ydo.ody.fake.FakeMatesEtaRepository
import com.ydo.ody.meetingId
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
class MeetingJoinViewModelTest {
    private lateinit var viewModel: MeetingJoinViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingJoinViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                inviteCode = "abc123",
                joinRepository = FakeJoinRepository(meetingId = meetingId),
                matesEtaRepository = FakeMatesEtaRepository,
            )
    }

    @Test
    fun `약속 참여에 성공하면 약속 방 화면으로 이동한다`() {
        // given
        setUpInitializeInfo()

        // when
        viewModel.joinMeeting()

        // then
        val actual = viewModel.navigateAction.getValue()
        assertThat(actual).isNotNull
        assertThat(actual).isEqualTo(MeetingJoinNavigateAction.JoinNavigateToRoom(meetingId))
    }

    @Test
    fun `입력하지 않은 값이 있는 경우 약속 참여할 수 없다`() {
        // when
        viewModel.joinMeeting()

        // then
        val actual = viewModel.navigateAction.getValue()
        assertThat(actual).isNull()
    }

    @Test
    fun `출발지 주소가 수도권이라면 유효하다`() {
        // given
        setUpInitializeInfo()

        // when
        viewModel.departureGeoLocation.value = GeoLocation(address = "인천광역시 남동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidDeparture.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `출발지 주소가 수도권이 아니라면 유효하지 않다`() {
        // given
        setUpInitializeInfo()

        // when
        viewModel.departureGeoLocation.value = GeoLocation(address = "부산광역시 동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidDeparture.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    private fun setUpInitializeInfo() {
        viewModel.departureGeoLocation.value = GeoLocation("인천광역시 남동구", "10.0", "10.0")
    }
}
