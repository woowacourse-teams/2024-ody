package com.mulberry.ody.presentation.feature.invitecode

import com.mulberry.ody.fake.FakeAnalyticsHelper
import com.mulberry.ody.fake.FakeMeetingRepository
import com.mulberry.ody.presentation.feature.invitecode.InviteCodeNavigateAction
import com.mulberry.ody.presentation.feature.invitecode.InviteCodeViewModel
import com.mulberry.ody.util.CoroutinesTestExtension
import com.mulberry.ody.util.InstantTaskExecutorExtension
import com.mulberry.ody.util.valueOnAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class InviteCodeViewModelTest {
    private val analyticsHelper = FakeAnalyticsHelper
    private val meetingRepository = FakeMeetingRepository
    private lateinit var viewModel: InviteCodeViewModel

    @BeforeEach
    fun setUp() {
        viewModel =
            InviteCodeViewModel(
                analyticsHelper,
                meetingRepository,
            )
    }

    @Test
    fun `초대 코드가 올바른 형식일 때 초대 코드를 체크하면 약속 참여 화면으로 이동한다`() {
        runTest {
            // given
            viewModel.inviteCode.value = "abcd1234"

            // when
            val actual =
                viewModel.navigateAction.valueOnAction {
                    viewModel.checkInviteCode()
                }

            // then
            assertThat(actual).isInstanceOf(InviteCodeNavigateAction.CodeNavigateToJoin::class.java)
        }
    }

    @Test
    fun `초대 코드가 올바르지 않은 형식일 때 에러 이벤트를 발생시킨다`() {
        runTest {
            // given
            viewModel.inviteCode.value = "qwerty1234532143214321"

            // when
            val actual =
                viewModel.invalidCodeEvent.valueOnAction {
                    viewModel.checkInviteCode()
                }

            // then
            assertThat(actual).isInstanceOf(String::class.java)
        }
    }
}
