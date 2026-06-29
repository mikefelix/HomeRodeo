package com.mozzarelly.homerodeo.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mozzarelly.homerodeo.R
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.data.model.Time
import com.mozzarelly.homerodeo.ui.theme.HomeRodeoTheme
import com.mozzarelly.homerodeo.ui.vm.AlarmActions
import com.mozzarelly.homerodeo.ui.vm.AlarmViewModel
import com.mozzarelly.homerodeo.util.FullCenteredRow
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.collectAsMutableState
import com.mozzarelly.homerodeo.util.composables.BodyText
import com.mozzarelly.homerodeo.util.composables.FullSizeUiWithSheet
import com.mozzarelly.homerodeo.util.composables.LocalImage
import com.mozzarelly.homerodeo.util.composables.PrimaryButton
import com.mozzarelly.homerodeo.util.composables.SecondaryButton
import com.mozzarelly.homerodeo.util.composables.TitleText
import com.mozzarelly.rodeo.ui.composables.ScrollingPicker

@Composable
fun AlarmScreen(
  viewModel: AlarmViewModel = hiltViewModel()
){
  val state by viewModel.state.collectAsState()
  val sheetState = viewModel.sheetState.collectAsMutableState()

  AlarmScreen(
    state = state,
    sheetState = sheetState,
    onRetry = {},
    actions = viewModel
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
  state: UiState<AlarmData>,
  sheetState: MutableState<Day?>,
  onRetry: () -> Unit,
  actions: AlarmActions
) {
  FullSizeUiWithSheet(
    state = state,
    onRetry = onRetry,
    sheetState = sheetState,
    sheetContent = { day ->
      AlarmBottomSheet(
        day = day,
        actions = actions
      )
    }
  ) { alarm ->
    Column(modifier = Modifier.fillMaxHeight()) {
      //TitleBar(title = null)

      FullCenteredRow {
        BodyText(
          text = alarm.desc,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
      }

      DayGrid(
        days = alarm.days,
        next = alarm.next,
        onClickDay = actions::editDay,
        modifier = Modifier
          .padding(16.dp)
      )

      if (alarm.allowDisableToday){
        Row(modifier = Modifier.padding(16.dp)) {
          PrimaryButton("I'm awake early!") {
            actions.disableToday()
          }
        }
      }

      if (alarm.allowTurnOff){
        Row(modifier = Modifier.padding(16.dp)) {
          PrimaryButton("Turn off alarm") {
            actions.turnOff()
          }
        }
      }
    }
  }
}

@Composable
private fun DayGrid(
  days: List<Day>,
  next: Day?,
  modifier: Modifier = Modifier,
  onClickDay: (Day) -> Unit
) {
  val size = days.size
  val grid = days.subList(0, size / 2) zip days.subList(size / 2, size)

  Column(modifier = modifier
    .border(1.dp, DividerDefaults.color)
  ) {
    grid.forEach { (day1, day2) ->
      Row(modifier = Modifier.height(IntrinsicSize.Min) ) {
        DayCell(day1, day1 == next, modifier = Modifier
          .weight(0.5f)
          .padding(8.dp)
        ) {
          onClickDay(day1)
        }

        HorizontalDivider(modifier = Modifier
          .fillMaxHeight()
          .width(1.dp))

        DayCell(day2, day2 == next, modifier = Modifier
          .weight(0.5f)
          .padding(8.dp)
        ) {
          onClickDay(day2)
        }
      }

      HorizontalDivider(thickness = 1.dp)
    }
  }
}

@Composable
private fun DayCell(day: Day, isNext: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
  Row(
    modifier = modifier
      .padding(8.dp)
      .clickable { onClick() }
  ) {
    BodyText(day.name,
      align = TextAlign.Left,
      italic = day.overridden,
      modifier = Modifier
    )

    if (isNext){
      LocalImage(
        R.drawable.ic_alarm,
        "next",
        modifier = Modifier.padding(start = 8.dp)
      )
    }

    BodyText(day.time?.toString() ?: "-",
      align = TextAlign.Right,
      italic = day.overridden,
      modifier = Modifier
        .weight(1f)
    )
  }
}

@Composable
private fun AlarmBottomSheet(day: Day, actions: AlarmActions) {
  val hours = (0..23).map { it.toString() }
  val minutes = (0..59).map { it.toString().padStart(2, '0') }

  val initialHour = day.time?.hour?.toIntOrNull() ?: 7
  val initialMinute = day.time?.minute?.toIntOrNull() ?: 0

  val hourState = remember { mutableIntStateOf(initialHour) }
  val minuteState = remember { mutableIntStateOf(initialMinute) }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 32.dp)
  ) {
    TitleText(
      day.name,
      modifier = Modifier.padding(vertical = 16.dp)
    )

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(vertical = 16.dp)
    ) {
      ScrollingPicker(
        items = hours,
        startIndex = initialHour,
        selectionState = hourState,
        modifier = Modifier.width(48.dp),
        textStyle = MaterialTheme.typography.titleLarge,
      )

      TitleText(":")

      ScrollingPicker(
        items = minutes,
        startIndex = initialMinute,
        selectionState = minuteState,
        modifier = Modifier.width(64.dp),
        textStyle = MaterialTheme.typography.titleLarge,
      )
    }

    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      modifier = Modifier.padding(horizontal = 16.dp)
    ) {
      SecondaryButton("Cancel") {
        actions.dismissEdit()
      }

      SecondaryButton("Disable") {
        actions.setTime(day, null, false)
        actions.dismissEdit()
      }

      PrimaryButton("Save") {
        val time = Time(hourState.intValue.toString(), minuteState.intValue.toString().padStart(2, '0'))
        actions.setTime(day, time, false)
        actions.dismissEdit()
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun AlarmBottomSheetPreview() {
  val mockDay = Day(
    num = 1,
    index = 1,
    time = Time("07", "30"),
    type = 'a'
  )
  val mockActions = object : AlarmActions {
    override fun editDay(day: Day) {}
    override fun dismissEdit() {}
    override fun setTime(day: Day, time: Time?, saveAsSetting: Boolean) {}
    override fun disableToday() {}
    override fun turnOff() {}
  }

  HomeRodeoTheme {
    Surface {
      AlarmBottomSheet(day = mockDay, actions = mockActions)
    }
  }
}
