package com.mulberry.ody.presentation.join

import com.mulberry.ody.Address
import com.mulberry.ody.fake.FakeAddressRepository
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeJoinRepository
import com.mulberry.ody.fake.FakeLocationHelper
import com.mulberry.ody.fake.FakeMatesEtaRepository
import com.mulberry.ody.meetingId
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
class MeetingJoinViewModelTest {
    private lateinit var viewModel: MeetingJoinViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingJoinViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                joinRepository = FakeJoinRepository(meetingId = meetingId),
                matesEtaRepository = FakeMatesEtaRepository,
                addressRepository = FakeAddressRepository,
                locationHelper = FakeLocationHelper,
            )
    }

    @Test
    fun `약속 참여에 성공하면 약속 방 화면으로 이동한다`() {
        runTest {
            // given
            setUpInitializeInfo()

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.onClickMeetingJoin("abc123")
                }

            // then
            assertThat(actual).isEqualTo(MeetingJoinNavigateAction.JoinNavigateToRoom(meetingId))
        }
    }

    @Test
    fun `입력하지 않은 값이 있는 경우 약속 참여할 수 없다`() {
        runTest {
            // when
            viewModel.onClickMeetingJoin("abc123")

            // then
            val actual = viewModel.navigateAction.valueOnAction {}
            assertThat(actual).isNull()
        }
    }

    @Test
    fun `출발지 주소가 수도권이라면 유효하다`() {
        runTest {
            // given
            setUpInitializeInfo()

            // when
            viewModel.departureAddress.value = Address(0, "인천광역시 남동구")

            // then
            assertThat(viewModel.isValidDeparture.first()).isTrue
        }
    }

    @Test
    fun `출발지 주소가 수도권이 아니라면 유효하지 않다`() {
        runTest {
            // given
            setUpInitializeInfo()

            // when
            viewModel.departureAddress.value = Address(0, "부산광역시 동구")

            // then
            assertThat(viewModel.isValidDeparture.first()).isFalse
        }
    }

    private fun setUpInitializeInfo() {
        viewModel.departureAddress.value = Address(0, "인천광역시 남동구")
    }
}
