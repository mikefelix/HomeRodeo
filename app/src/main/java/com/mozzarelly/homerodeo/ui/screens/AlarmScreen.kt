package com.mozzarelly.homerodeo.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mozzarelly.homerodeo.R
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.ui.vm.AlarmActions
import com.mozzarelly.homerodeo.ui.vm.AlarmViewModel
import com.mozzarelly.homerodeo.util.FullCenteredRow
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.composables.BodyText
import com.mozzarelly.homerodeo.util.composables.FullSizeUi
import com.mozzarelly.homerodeo.util.composables.LocalImage
import com.mozzarelly.homerodeo.util.composables.PrimaryButton
import com.mozzarelly.homerodeo.util.composables.TitleBar

@Composable
fun AlarmScreen(
  viewModel: AlarmViewModel = hiltViewModel()
){
  val state by viewModel.state.collectAsState()

  AlarmScreen(
    state = state,
    onRetry = {},
    actions = viewModel
  )
}

@Composable
fun AlarmScreen(
  state: UiState<AlarmData>,
  onRetry: () -> Unit,
  actions: AlarmActions
) {
  val dayUnderEdit = remember { mutableStateOf<Day?>(null) }

  FullSizeUi(state, onRetry = onRetry) { alarm ->
    Column(modifier = Modifier.fillMaxHeight()) {
      TitleBar()

      FullCenteredRow {
        BodyText(alarm.desc,
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        )
      }

      DayGrid(
        alarm.days,
        alarm.next,
        modifier = Modifier
          .padding(16.dp)
      ) {
        dayUnderEdit.value = it
      }

      if (alarm.allowDisableToday){
        Row(modifier = Modifier.padding(16.dp)) {
          PrimaryButton("I'm awake early!") {
            actions.disableToday()
          }
        }
      }
    }
  }

/*  BottomSheetForState(dayUnderEdit, expandFully = true) {
    AlarmBottomSheet(it, actions)
  }*/
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
fun DayCell(day: Day, isNext: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
