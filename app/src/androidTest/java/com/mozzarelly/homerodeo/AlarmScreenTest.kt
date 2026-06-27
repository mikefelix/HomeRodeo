package com.mozzarelly.homerodeo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.data.model.Time
import com.mozzarelly.homerodeo.ui.screens.AlarmScreen
import com.mozzarelly.homerodeo.ui.vm.AlarmActions
import com.mozzarelly.homerodeo.util.UiState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class AlarmScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var actions: AlarmActions

    @Before
    fun setUp() {
        actions = mock()
    }

    private fun makeAlarm(on: Boolean = false, nextNum: Int = 1): AlarmData {
        val days = (0..13).map { i ->
            Day(
                num = i,
                index = i % 7,
                time = if (i % 7 < 5) Time("7", "30") else null,
                type = 'f',
                disabled = i % 7 >= 5,
            )
        }
        return AlarmData(
            on = on,
            nextNum = nextNum,
            days = days,
            today = days[0],
        )
    }

    @Test
    fun showsAlarmDescriptionWhenStateIsSuccess() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(makeAlarm()),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("The alarm is", substring = true).assertIsDisplayed()
    }

    @Test
    fun showsDayNamesWhenStateIsSuccess() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(makeAlarm()),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("Mon").assertIsDisplayed()
        composeRule.onNodeWithText("Tue").assertIsDisplayed()
        composeRule.onNodeWithText("Wed").assertIsDisplayed()
        composeRule.onNodeWithText("Thu").assertIsDisplayed()
        composeRule.onNodeWithText("Fri").assertIsDisplayed()
    }

    @Test
    fun showsAlarmTimeWhenDayHasTime() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(makeAlarm()),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("7:30").assertIsDisplayed()
    }

    @Test
    fun showsOffWhenDayHasNoTime() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(makeAlarm()),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("off").assertIsDisplayed()
    }

    @Test
    fun showsErrorAndRetryWhenStateIsError() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.error("Network error"),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("Sorry, there was an error", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun hidesAlarmDescriptionWhenStateIsLoading() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.loading(),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("The alarm is", substring = true).assertIsNotDisplayed()
    }

    @Test
    fun showsDisableTodayButtonWhenAlarmIsRingingToday() {
        val alarm = makeAlarm(on = true, nextNum = 0)
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(alarm),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("I'm awake early!").assertIsDisplayed()
    }

    @Test
    fun hidesDisableTodayButtonWhenAlarmIsNotRinging() {
        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(makeAlarm(on = false)),
                sheetState = remember { mutableStateOf(null) },
                onRetry = {},
                actions = actions,
            )
        }
        composeRule.onNodeWithText("I'm awake early!").assertDoesNotExist()
    }

    @Test
    fun bottomSheetShowsCorrectDayWhenDayRowClicked() {
        val alarm = makeAlarm()
        val dayUnderEdit = mutableStateOf<Day?>(null)

        // Delegate to the mock for recording, but also update dayUnderEdit so the sheet opens.
        val delegatingActions = object : AlarmActions {
            override fun editDay(day: Day) {
                dayUnderEdit.value = day
                actions.editDay(day)
            }
            override fun dismissEdit() = actions.dismissEdit()
            override fun setTime(day: Day, time: Time?, saveAsSetting: Boolean) = actions.setTime(day, time, saveAsSetting)
            override fun disableToday() = actions.disableToday()
        }

        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(alarm),
                sheetState = dayUnderEdit,
                onRetry = {},
                actions = delegatingActions,
            )
        }

        composeRule.onNodeWithText("Save").assertDoesNotExist()

        composeRule.onAllNodesWithText("Wed")[0].performClick()

        verify(actions).editDay(any())

        composeRule.onNodeWithText("Save").assertIsDisplayed()
        composeRule.onNodeWithText("Cancel").assertIsDisplayed()
        // "Wed" appears in both the grid and the bottom sheet title
        assertEquals(2, composeRule.onAllNodesWithText("Wed").fetchSemanticsNodes().size)
    }

    @Test
    fun bottomSheetCallsSetTimeWhenSaveClicked() {
        val alarm = makeAlarm()
        val dayUnderEdit = alarm.days[2] // Wed

        composeRule.setContent {
            AlarmScreen(
                state = UiState.success(alarm),
                sheetState = remember { mutableStateOf(dayUnderEdit) },
                onRetry = {},
                actions = actions,
            )
        }

        composeRule.onNodeWithText("Save").performClick()

        verify(actions).setTime(eq(dayUnderEdit), any(), eq(false))
        verify(actions).dismissEdit()
    }
}
