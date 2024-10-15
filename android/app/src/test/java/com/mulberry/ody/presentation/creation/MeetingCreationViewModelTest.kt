package com.mulberry.ody.presentation.creation

import com.mulberry.ody.Address
import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.inviteCode
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
        runTest {
            // given
            val nowTime = LocalTime.now()

            // when
            viewModel.initializeMeetingTime()

            // then
            assertThat(viewModel.meetingHour.first()).isEqualTo(nowTime.hour)
            assertThat(viewModel.meetingMinute.first()).isEqualTo(nowTime.minute)
        }
    }

    @Test
    fun `약속을 생성한다`() {
        runTest {
            // given
            setUpInitializeInfo()

            // when
            viewModel.createMeeting()

            // then
            assertThat(viewModel.inviteCode.first()).isEqualTo(inviteCode)
        }
    }

    @Test
    fun `입력하지 않은 값이 있는 경우 약속이 생성되지 않는다`() {
        runTest {
            // when
            viewModel.createMeeting()

            // then
            assertThat(viewModel.inviteCode.first()).isBlank()
        }
    }

    @Test
    fun `약속 이름이 1자에서 15자인 경우 유효하다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.NAME

            // when
            viewModel.meetingName.value = "카키와 술 먹기"

            // then
            assertThat(viewModel.isValidInfo.first()).isTrue
        }
    }

    @Test
    fun `약속 이름이 1자에서 15자가 아닌 경우 유효하지 않다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.NAME

            // when
            viewModel.meetingName.value = "카키와 술 먹기 카키와 술 먹기 카키와 술 먹기"

            // then
            assertThat(viewModel.isValidInfo.first()).isFalse
        }
    }

    @Test
    fun `약속 날짜가 오늘보다 이전인 경우 선택할 수 없다`() {
        runTest {
            // given
            val meetingDate = LocalDate.of(2023, 7, 28)

            // when
            val actual = viewModel.invalidMeetingDateEvent.valueOnAction {
                viewModel.updateMeetingDate(meetingDate)
            }

            // then
            assertThat(actual).isNotNull
        }
    }

    @Test
    fun `약속 날짜가 오늘과 같은 경우 선택할 수 있다`() {
        runTest {
            // given
            val meetingDate = LocalDate.now()

            // when
            viewModel.updateMeetingDate(meetingDate)

            // then
            assertThat(viewModel.meetingDate.first()).isEqualTo(LocalDate.now())
        }
    }

    @Test
    fun `약속 날짜가 오늘보다 이후인 경우 선택할 수 있다`() {
        runTest {
            // given
            val meetingDate = LocalDate.of(2030, 7, 28)

            // when
            viewModel.updateMeetingDate(meetingDate)

            // then
            assertThat(viewModel.meetingDate.first()).isEqualTo(LocalDate.of(2030, 7, 28))
        }
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이후인 경우 유효하다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.TIME

            // when
            viewModel.meetingDate.value = LocalDate.of(2030, 7, 28)
            viewModel.meetingHour.value = 18
            viewModel.meetingMinute.value = 0

            // then
            assertThat(viewModel.isValidInfo.first()).isTrue
        }
    }

    @Test
    fun `약속 날짜와 시간이 현재보다 이전인 경우 유효하지 않다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.TIME

            // when
            viewModel.meetingDate.value = LocalDate.of(2023, 7, 28)
            viewModel.meetingHour.value = 17
            viewModel.meetingMinute.value = 0

            // then
            assertThat(viewModel.isValidInfo.first()).isFalse
        }
    }

    @Test
    fun `약속 장소가 수도권인 경우 유효하다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION

            // when
            viewModel.destinationAddress.value = Address(0, "인천광역시 남동구")

            // then
            assertThat(viewModel.isValidInfo.first()).isTrue
        }
    }

    @Test
    fun `약속 장소가 수도권이 아닌 경우 유효하지 않다`() {
        runTest {
            // given
            setUpInitializeInfo()
            viewModel.meetingCreationInfoType.value = MeetingCreationInfoType.DESTINATION

            // when
            viewModel.destinationAddress.value = Address(0, "부산광역시 동구")

            // then
            assertThat(viewModel.isValidInfo.first()).isFalse
        }
    }

    private fun setUpInitializeInfo() {
        viewModel.meetingName.value = "올리브와 마라탕 먹기"
        viewModel.meetingDate.value = LocalDate.of(2030, 7, 28)
        viewModel.meetingHour.value = 18
        viewModel.meetingMinute.value = 0
        viewModel.destinationAddress.value = Address(id = 0, roadNameAddress = "인천광역시 남동구")
        viewModel.isValidInfo.value = true
    }
}
