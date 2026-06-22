package com.mozzarelly.homerodeo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TabsViewModel @Inject constructor() : ViewModel() {
  var currentDestination by mutableStateOf<Tab?>(null)
    private set

  fun updateDestination(tab: Tab) {
    currentDestination = tab
  }

  val tabs = listOf(
    Tab("Summary", R.drawable.ic_home),
    Tab("Devices", R.drawable.ic_devices),
    Tab("Alarm", R.drawable.ic_alarm),
    Tab("Fermenter", R.drawable.ic_beer),
    Tab("Weather", R.drawable.ic_weather),
  )
}
