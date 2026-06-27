package com.mozzarelly.homerodeo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mozzarelly.homerodeo.util.composables.TitleText

@Composable
fun WeatherScreen(
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
  ){
    TitleText("Weather")
  }
}
