package com.woowacourse.ody.presentation.join

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
import com.woowacourse.ody.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MeetingJoinActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MeetingJoinActivity::class.java)

    @Test
    fun `아무것도_입력하지_않으면_다음_버튼이_비활성화_된다`() {
        // given
        val nicknameEditText = onView(withId(R.id.et_nickname))
        val nextButton = onView(withId(R.id.btn_next))

        // when
        nicknameEditText
            .perform(clearText())

        // then
        nextButton
            .check(matches(isNotEnabled()))
    }

    @Test
    fun `10글자의_닉네임을_입력하면_9글자까지만_보인다`() {
        // given
        val nicknameEditText = onView(withId(R.id.et_nickname))

        // when
        nicknameEditText
            .perform(replaceText("안녕하세요안녕하세요"))

        // then
        nicknameEditText
            .check(matches(withText("안녕하세요안녕하세")))
    }

    @Test
    fun `닉네임을_입력하면_다음_버튼이_활성화_된다`() {
        // given
        val nicknameEditText = onView(withId(R.id.et_nickname))
        val nextButton = onView(withId(R.id.btn_next))

        // when
        nicknameEditText
            .perform(replaceText("올리브"))

        // then
        nextButton
            .check(matches(isEnabled()))
    }

    @Test
    fun `닉네임을_입력하면_엑스_버튼이_보인다`() {
        // given
        val nicknameEditText = onView(withId(R.id.et_nickname))
        val cancelButton = onView(withId(R.id.iv_cancel))

        // when
        nicknameEditText
            .perform(replaceText("올리브"))

        // then
        cancelButton
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `엑스_버튼을_클릭하면_입력한_닉네임이_지워진다`() {
        // given
        val nicknameEditText = onView(withId(R.id.et_nickname))
        val cancelButton = onView(withId(R.id.iv_cancel))
        nicknameEditText
            .perform(replaceText("올리브"))

        // when
        cancelButton
            .perform(click())

        // then
        nicknameEditText
            .check(matches(withText("")))
    }
}
