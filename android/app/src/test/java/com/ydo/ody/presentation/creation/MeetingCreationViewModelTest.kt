package com.ydo.ody.presentation.creation

import com.ydo.ody.domain.model.GeoLocation
import com.ydo.ody.fake.FakeAnalyticsHelper
import com.ydo.ody.fake.FakeMeetingRepository
import com.ydo.ody.inviteCode
import com.ydo.ody.util.CoroutinesTestExtension
import com.ydo.ody.util.InstantTaskExecutorExtension
import com.ydo.ody.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.time.LocalTime

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class MeetingCreationViewModelTest {
    private lateinit var viewModel: MeetingCreationViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            MeetingCreationViewModel(
                analyticsHelper = FakeAnalyticsHelper,
                meetingRepository = FakeMeetingRepository,
            )
    }

    @Test
    fun `약속 시간의 초깃값을 현재로 설정한다`() {
        // given
        val nowTime = LocalTime.now()

        // when
        viewModel.initializeMeetingTime()

        // then
        val hour = viewModel.meetingHour.getOrAwaitValue()
        val minute = viewModel.meetingMinute.getOrAwaitValue()
        assertThat(hour).isEqualTo(nowTime.hour)
        assertThat(minute).isEqualTo(nowTime.minute)
    }

    @Test
    fun `약속을 생성한다`() {
        // given
        setUpInitializeInfo()

        // when
        viewModel.createMeeting()

        // then
        val actual = viewModel.inviteCode.getOrAwaitValue()
        assertThat(actual).isEqualTo(inviteCode)
    }

    @Test
    fun `입력하지 않은 값이 있는 경우 약속이 생성되지 않는다`() {
        // when
        viewModel.createMeeting()

        // then
        assertThat(viewModel.inviteCode.isInitialized).isFalse
    }

    @Test
    fun `약속 이름이 1자에서 15자인 경우 유효하다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.NAME

        // when
        viewModel.meetingName.value = "카키와 술 먹기"

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `약속 이름이 1자에서 15자가 아닌 경우 유효하지 않다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.NAME

        // when
        viewModel.meetingName.value = "카키와 술 먹기 카키와 술 먹기 카키와 술 먹기"

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    @Test
    fun `약속 날짜가 오늘보다 이전인 경우 선택할 수 없다`() {
        // given
        val meetingDate = LocalDate.of(2023, 7, 28)

        // when
        viewModel.updateMeetingDate(meetingDate)

        // then
        val actual = viewModel.invalidMeetingDateEvent.getValue()
        assertThat(actual).isNotNull
    }

    @Test
    fun `약속 날짜가 오늘과 같은 경우 선택할 수 있다`() {
        // given
        val meetingDate = LocalDate.now()

        // when
        viewModel.updateMeetingDate(meetingDate)

        // then
        val actual = viewModel.meetingDate.getOrAwaitValue()
        assertThat(actual).isEqualTo(LocalDate.now())
    }

    @Test
    fun `약속 날짜가 오늘보다 이후인 경우 선택할 수 있다`() {
        // given
        val meetingDate = LocalDate.of(2030, 7, 28)

        // when
        viewModel.updateMeetingDate(meetingDate)

        // then
        val actual = viewModel.meetingDate.getOrAwaitValue()
        assertThat(actual).isEqualTo(LocalDate.of(2030, 7, 28))
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이후인 경우 유효하다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.TIME

        // when
        viewModel.meetingDate.value = LocalDate.of(2030, 7, 28)
        viewModel.meetingHour.value = 18
        viewModel.meetingMinute.value = 0

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이전인 경우 유효하지 않다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.TIME

        // when
        viewModel.meetingDate.value = LocalDate.of(2023, 7, 28)
        viewModel.meetingHour.value = 18
        viewModel.meetingMinute.value = 0

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    @Test
    fun `약속 장소가 수도권인 경우 유효하다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION

        // when
        viewModel.destinationGeoLocation.value = GeoLocation(address = "인천광역시 남동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isTrue
    }

    @Test
    fun `약속 장소가 수도권이 아닌 경우 유효하지 않다`() {
        // given
        setUpInitializeInfo()
        viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION

        // when
        viewModel.destinationGeoLocation.value = GeoLocation(address = "부산광역시 동구", "0.0", "0.0")

        // then
        val actual = viewModel.isValidInfo.getOrAwaitValue()
        assertThat(actual).isFalse
    }

    private fun setUpInitializeInfo() {
        viewModel.meetingName.value = "올리브와 마라탕 먹기"
        viewModel.meetingDate.value = LocalDate.of(2030, 7, 28)
        viewModel.meetingHour.value = 18
        viewModel.meetingMinute.value = 0
        viewModel.destinationGeoLocation.value = GeoLocation("인천광역시 남동구", "10.0", "10.0")
        viewModel.isValidInfo.value = true
    }
}
