package com.codingblocks.cbonlineapp.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.codingblocks.cbonlineapp.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest{

    @get:Rule
    val actRule = ActivityTestRule<SettingsActivity>(SettingsActivity::class.java)

    @Test
    fun testSwitch(){
        onView(withId(R.id.wifiSwitch)).check(matches(isDisplayed())).perform(click()).perform()
    }

}