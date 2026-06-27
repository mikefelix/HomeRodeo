package com.mozzarelly.homerodeo.ui.screens

import androidx.compose.runtime.Composable
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.composables.FullSizeUiWithSheet
import com.mozzarelly.homerodeo.util.composables.TitleText

@Composable
fun SummaryScreen(

) {
  FullSizeUiWithSheet(
    state = UiState("Summary"),
    onRetry = {},
    sheetContent = { _: String -> }
  ){
    TitleText("Summary")
  }
}