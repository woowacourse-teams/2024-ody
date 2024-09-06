package com.ydo.ody.presentation.creation

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
class MeetingCreationActivityTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MeetingCreationActivity::class.java)

    @Test
    fun `아무것도_입력하지_않으면_다음_버튼이_비활성화_된다`() {
        // given
        val meetingNameEditText = onView(withId(R.id.et_meeting_name))
        val nextButton = onView(withId(R.id.btn_next))

        // when
        meetingNameEditText
            .perform(clearText())

        // then
        nextButton
            .check(matches(isNotEnabled()))
    }

    @Test
    fun `17글자의_약속_이름을_입력하면_15글자까지만_보인다`() {
        // given
        val meetingNameEditText = onView(withId(R.id.et_meeting_name))

        // when
        meetingNameEditText
            .perform(replaceText("안녕하세요안녕하세요안녕하세요안녕"))

        // then
        meetingNameEditText
            .check(matches(withText("안녕하세요안녕하세요안녕하세요")))
    }

    @Test
    fun `약속_이름을_입력하면_다음_버튼이_활성화_된다`() {
        // given
        val meetingNameEditText = onView(withId(R.id.et_meeting_name))
        val nextButton = onView(withId(R.id.btn_next))

        // when
        meetingNameEditText
            .perform(replaceText("123abc"))

        // then
        nextButton
            .check(matches(isEnabled()))
    }

    @Test
    fun `약속_이름을_입력하면_엑스_버튼이_보인다`() {
        // given
        val meetingNameEditText = onView(withId(R.id.et_meeting_name))
        val cancelButton = onView(withId(R.id.iv_cancel))

        // when
        meetingNameEditText
            .perform(replaceText("123abc"))

        // then
        cancelButton
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun `엑스_버튼을_클릭하면_입력한_약속_이름이_지워진다`() {
        // given
        val meetingNameEditText = onView(withId(R.id.et_meeting_name))
        val cancelButton = onView(withId(R.id.iv_cancel))
        meetingNameEditText
            .perform(replaceText("123abc"))

        // when
        cancelButton
            .perform(click())

        // then
        meetingNameEditText
            .check(matches(withText("")))
    }
}
