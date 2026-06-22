package com.mozzarelly.homerodeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.mozzarelly.homerodeo.ui.theme.HomeRodeoTheme
import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
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

//@Parcelize
data class Tab(
  val label: String,
  val icon: Int,
)/* : Parcelable*/ {
}
