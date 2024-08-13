package com.woowacourse.ody.viewmodel

import com.woowacourse.ody.domain.model.GeoLocation
import com.woowacourse.ody.fake.FakeAnalyticsHelper
import com.woowacourse.ody.fake.FakeJoinRepository
import com.woowacourse.ody.fake.FakeMatesEtaRepository
import com.woowacourse.ody.presentation.join.MeetingJoinInfoType
import com.woowacourse.ody.presentation.join.MeetingJoinNavigateAction
import com.woowacourse.ody.presentation.join.MeetingJoinViewModel
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
class MeetingJoinViewModelTest {
    private lateinit var viewModel: MeetingJoinViewModel
    private val meetingId: Long = 0L

    @BeforeEach
    fun setUp() {
        viewModel = MeetingJoinViewModel(
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
    fun `닉네임이 1~9자 사이라면 유효하다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingJoinInfoType.value = MeetingJoinInfoType.NICKNAME

        // when
        viewModel.nickname.value = "올리브"

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `닉네임이 1~9자 사이가 아니라면 유효하지 않다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingJoinInfoType.value = MeetingJoinInfoType.NICKNAME

        // when
        viewModel.nickname.value = "올리브올리브올리브올리브올리브"

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    @Test
    fun `출발지 주소가 수도권이라면 유효하다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingJoinInfoType.value = MeetingJoinInfoType.DEPARTURE

        // when
        viewModel.departureGeoLocation.value = GeoLocation(address = "인천광역시 남동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `출발지 주소가 수도권이 아니라면 유효하지 않다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingJoinInfoType.value = MeetingJoinInfoType.DEPARTURE

        // when
        viewModel.departureGeoLocation.value = GeoLocation(address = "부산광역시 동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    private fun setUpInitializeInfo() {
        viewModel.nickname.value = "올리브"
        viewModel.departureGeoLocation.value = GeoLocation("인천광역시 남동구", "10.0", "10.0")
        viewModel.isValidInfo.value = true
    }
}
