package com.woowacourse.ody.viewmodel

import com.woowacourse.ody.fake.FakeAnalyticsHelper
import com.woowacourse.ody.fake.FakeMeetingRepository
import com.woowacourse.ody.presentation.invitecode.InviteCodeNavigateAction
import com.woowacourse.ody.presentation.invitecode.InviteCodeViewModel
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
        // given
        viewModel.inviteCode.value = "abcd1234"

        // when
        viewModel.checkInviteCode()

        // then
        assertThat(
            viewModel.navigateAction.getOrAwaitValue(),
        ).isInstanceOf(InviteCodeNavigateAction.CodeNavigateToJoin::class.java)
    }

    @Test
    fun `초대 코드가 올바르지 않은 형식일 때 에러 이벤트를 발생시킨다`() {
        // given
        viewModel.inviteCode.value = "qwerty1234532143214321"

        // when
        viewModel.checkInviteCode()

        // then
        assertThat(
            viewModel.invalidInviteCodeEvent.getOrAwaitValue(),
        ).isInstanceOf(Unit::class.java)
    }
}
