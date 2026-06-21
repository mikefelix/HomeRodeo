package com.example.homerodeo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class TabsViewModel : ViewModel() {
  var currentDestination by mutableStateOf<Tab?>(null)
    private set

  fun updateDestination(tab: Tab) {
    currentDestination = tab
  }

  val tabs: List<Tab> = listOf(
    Tab("Home", R.drawable.ic_home),
    Tab("Favorites", R.drawable.ic_favorite),
    Tab("Profile", R.drawable.ic_account_box),
  )
}
