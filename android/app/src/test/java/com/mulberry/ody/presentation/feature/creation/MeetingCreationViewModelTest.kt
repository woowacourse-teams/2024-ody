package com.mulberry.ody.presentation.feature.creation

import android.location.Location
import com.mulberry.ody.domain.model.Address
import com.mulberry.ody.fake.FakeAddressRepository
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeLocationHelper
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.presentation.feature.creation.model.MeetingCreationNavigateAction
import com.mulberry.ody.util.CoroutinesTestExtension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
class MeetingCreationViewModelTest {
    private lateinit var viewModel: MeetingCreationViewModel
    private val fakeAddress = Address(
        id = 1L,
        placeName = "사당역",
        detailAddress = "서울 동작구",
        latitude = "",
        longitude = "",
    )
    private val fakeCurrentLocation = Location("test_provider").apply {
        latitude = 12345.0
        longitude = 12345.0
    }

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingCreationViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                meetingRepository = FakeMeetingRepository,
                addressRepository = FakeAddressRepository,
                locationHelper = FakeLocationHelper(fakeCurrentLocation),
            )
    }

    @Test
    fun `초기에는 약속을 생성할 수 없다`() = runTest {
        // when
        val actual = viewModel.isCreationValid.first()

        // then
        assertThat(actual).isFalse
    }

    @Test
    fun `약속 이름을 설정한다`() = runTest {
        // when
        viewModel.updateMeetingName("올리브와 마라탕 먹기")

        // then
        val actual = viewModel.meetingCreationUiModel.first().name
        assertThat(actual).isEqualTo("올리브와 마라탕 먹기")
    }

    @Test
    fun `약속 날짜의 초기값은 오늘과 동일하다`() = runTest {
        // when
        val actual = viewModel.meetingCreationUiModel.first().date

        // then
        assertThat(actual).isEqualTo(LocalDate.now())
    }

    @Test
    fun `약속 날짜를 설정한다`() = runTest {
        // when
        viewModel.updateMeetingDate(LocalDate.now().plusDays(3))

        // then
        val actual = viewModel.meetingCreationUiModel.first().date
        assertThat(actual).isEqualTo(LocalDate.now().plusDays(3))
    }

    @Test
    fun `약속 시간의 초기값은 오늘과 동일하다`() = runTest {
        // when
        val actual = viewModel.meetingCreationUiModel.first().time

        // then
        assertThat(actual.hour).isEqualTo(LocalTime.now().hour)
        assertThat(actual.minute).isEqualTo(LocalTime.now().minute)
    }

    @Test
    fun `약속 시간을 설정한다`() = runTest {
        // given
        val expected = LocalTime.now().plusHours(10)

        // when
        viewModel.updateMeetingTime(expected)

        // then
        val actual = viewModel.meetingCreationUiModel.first().time
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `현재 위치를 가져와서 약속 장소로 설정한다`() = runTest {
        // when
        viewModel.getDefaultLocation()

        // then
        val actual = viewModel.meetingCreationUiModel.first().destination
        assertThat(actual?.latitude).isEqualTo(fakeCurrentLocation.latitude.toString())
        assertThat(actual?.longitude).isEqualTo(fakeCurrentLocation.longitude.toString())
    }

    @Test
    fun `약속 장소를 설정한다`() = runTest {
        // when
        viewModel.updateMeetingDestination(fakeAddress)

        // then
        val actual = viewModel.meetingCreationUiModel.first().destination
        assertThat(actual).isEqualTo(fakeAddress)
    }

    @Test
    fun `모든 값이 설정되면 약속을 생성할 수 있다`() = runTest {
        // when
        viewModel.updateMeetingName("올리브와 마라탕 먹기")
        viewModel.updateMeetingDate(LocalDate.now().plusDays(3))
        viewModel.updateMeetingTime(LocalTime.now().plusHours(10))
        viewModel.updateMeetingDestination(fakeAddress)

        // then
        val actual = viewModel.isCreationValid.first()
        assertThat(actual).isTrue
    }

    @Test
    fun `약속을 생성하면 약속 참여 화면으로 이동한다`() = runTest {
        // given
        viewModel.updateMeetingName("올리브와 마라탕 먹기")
        viewModel.updateMeetingDate(LocalDate.now().plusDays(3))
        viewModel.updateMeetingTime(LocalTime.now().plusHours(10))
        viewModel.updateMeetingDestination(fakeAddress)

        // when
        launch {
            viewModel.createMeeting()
        }

        // then
        val actual = viewModel.navigateAction.first()
        assertThat(actual).isInstanceOf(MeetingCreationNavigateAction.NavigateToMeetingJoin::class.java)
    }
}
