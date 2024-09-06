package com.ydo.ody.presentation.invitecode

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotEnabled
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ydo.ody.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InviteCodeActivityTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<InviteCodeActivity> = ActivityScenarioRule(InviteCodeActivity::class.java)

    @Test
    fun `아무것도_입력하지_않으면_확인_버튼이_비활성화_된다`() {
        // given
        val inviteCodeEditText = onView(withId(R.id.et_invite_code))
        val confirmButton = onView(withId(R.id.btn_confirm))

        // when
        inviteCodeEditText
            .perform(clearText())

        // then
        confirmButton
            .check(matches(isNotEnabled()))
    }

    @Test
    fun `초대_코드를_입력하면_확인_버튼이_활성화_된다`() {
        // given
        val inviteCodeEditText = onView(withId(R.id.et_invite_code))
        val confirmButton = onView(withId(R.id.btn_confirm))

        // when
        inviteCodeEditText
            .perform(replaceText("123abc"))

        // then
        confirmButton
            .check(matches(isEnabled()))
    }

    @Test
    fun `초대_코드를_입력하면_엑스_버튼이_보인다`() {
        // given
        val inviteCodeEditText = onView(withId(R.id.et_invite_code))
        val cancelButton = onView(withId(R.id.iv_cancel))

        // when
        inviteCodeEditText
            .perform(replaceText("123abc"))

        // then
        cancelButton
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `엑스_버튼을_클릭하면_입력한_초대_코드가_지워진다`() {
        // given
        val inviteCodeEditText = onView(withId(R.id.et_invite_code))
        val cancelButton = onView(withId(R.id.iv_cancel))
        inviteCodeEditText
            .perform(replaceText("123abc"))

        // when
        cancelButton
            .perform(click())

        // then
        inviteCodeEditText
            .check(matches(withText("")))
    }
}
