package com.mulberry.ody.presentation.feature.join

import android.location.Location
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.fake.FakeAddressRepository
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeJoinRepository
import com.mulberry.ody.fake.FakeLocationHelper
import com.mulberry.ody.fake.FakeMatesEtaRepository
import com.mulberry.ody.meetingId
import com.mulberry.ody.presentation.feature.join.model.MeetingJoinNavigateAction
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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

    private val fakeAddress =
        Address(
            id = 1L,
            placeName = "사당역",
            detailAddress = "서울 동작구",
            latitude = "",
            longitude = "",
        )
    private val fakeCurrentLocation =
        Location("test_provider").apply {
            latitude = 12345.0
            longitude = 12345.0
        }

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingJoinViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                joinRepository = FakeJoinRepository(meetingId = meetingId),
                addressRepository = FakeAddressRepository,
                locationHelper = FakeLocationHelper(fakeCurrentLocation),
                matesEtaRepository = FakeMatesEtaRepository,
            )
    }

    @Test
    fun `출발 장소가 설정되지 않으면 약속에 참여할 수 없다`() =
        runTest {
            // when
            val actual = viewModel.isJoinValid.first()

            // then
            assertThat(actual).isFalse
        }

    @Test
    fun `현재 위치를 가져와서 출발 장소로 설정한다`() =
        runTest {
            // when
            viewModel.getCurrentLocation()

            // then
            val actual = viewModel.departureAddress.first()
            assertThat(actual?.latitude).isEqualTo(fakeCurrentLocation.latitude.toString())
            assertThat(actual?.longitude).isEqualTo(fakeCurrentLocation.longitude.toString())
        }

    @Test
    fun `출발지 주소를 설정한다`() =
        runTest {
            // when
            viewModel.updateMeetingDeparture(fakeAddress)

            // then
            val actual = viewModel.departureAddress.first()
            assertThat(actual).isEqualTo(fakeAddress)
        }

    @Test
    fun `출발 장소가 설정되면 약속에 참여할 수 있다`() =
        runTest {
            // when
            viewModel.updateMeetingDeparture(fakeAddress)

            // then
            val actual = viewModel.isJoinValid.first()
            assertThat(actual).isTrue
        }

    @Test
    fun `약속에 참여하면 약속 방 화면으로 이동한다`() =
        runTest {
            // given
            viewModel.updateMeetingDeparture(fakeAddress)

            // when
            launch {
                viewModel.joinMeeting("abc123")
            }

            // then
            val actual = viewModel.navigateAction.first()
            assertThat(actual).isEqualTo(MeetingJoinNavigateAction.JoinNavigateToRoom(meetingId))
        }
}
