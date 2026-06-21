package com.example.homerodeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.example.homerodeo.ui.theme.HomeRodeoTheme

abstract class BottomTabsActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HomeRodeoTheme {
        Content()
      }
    }
  }

  @Composable
  abstract fun Content()

  abstract val viewModel: TabsViewModel
}

data class Tab(
  val label: String,
  val icon: Int,
) /*{
  HOME("Home", R.drawable.ic_home),
  FAVORITES("Favorites", R.drawable.ic_favorite),
  PROFILE("Profile", R.drawable.ic_account_box),
}*/


