package com.mozzarelly.homerodeo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<RodeoActivity>()

    @Test
    fun allTabsAreVisible() {
        composeRule.onNodeWithText("Summary").assertIsDisplayed()
        composeRule.onNodeWithText("Devices").assertIsDisplayed()
        composeRule.onNodeWithText("Alarm").assertIsDisplayed()
        composeRule.onNodeWithText("Fermenter").assertIsDisplayed()
        composeRule.onNodeWithText("Weather").assertIsDisplayed()
    }

    @Test
    fun clickingAlarmTabShowsAlarmScreen() {
        composeRule.onNodeWithText("Alarm").performClick()

        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("The alarm is", substring = true)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeRule.onNodeWithText("The alarm is", substring = true).assertIsDisplayed()
    }
}
