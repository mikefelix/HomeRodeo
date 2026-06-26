package com.mozzarelly.homerodeo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.mozzarelly.homerodeo.ui.screens.AlarmScreen
import com.mozzarelly.homerodeo.ui.screens.DevicesScreen
import com.mozzarelly.homerodeo.ui.screens.FermenterScreen
import com.mozzarelly.homerodeo.ui.screens.SummaryScreen
import com.mozzarelly.homerodeo.ui.screens.WeatherScreen
import com.mozzarelly.homerodeo.ui.theme.HomeRodeoTheme

@AndroidEntryPoint
class RodeoActivity : BottomTabsActivity() {

  override val viewModel by viewModels<TabsViewModel>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      HomeRodeoTheme {
        HomeRodeoApp(viewModel)
      }
    }
  }

  @Composable
  override fun Content() {
    HomeRodeoApp(viewModel)
  }

}

@PreviewScreenSizes
@Composable
fun HomeRodeoApp(
  viewModel: TabsViewModel = TabsViewModel(),
) {
  var currentDestination by rememberSaveable { mutableStateOf(viewModel.tabs.first().label) }
  val currentTab = remember(currentDestination) { viewModel.tabs.find { it.label == currentDestination }!! }
  val tabs = viewModel.tabs

  NavigationSuiteScaffold(
    navigationSuiteItems = {
      tabs.forEach {
        item(
          icon = {
            Icon(
              painterResource(it.icon),
              contentDescription = it.label
            )
          },
          label = { Text(it.label) },
          selected = it == currentTab,
          onClick = { currentDestination = it.label }
        )
      }
    }
  ) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
      TabContent(
        tab = currentTab,
        modifier = Modifier.padding(innerPadding)
      )
    }
  }
}

@Composable
fun TabContent(tab: Tab, modifier: Modifier) {
  when (tab.label) {
    "Summary" -> SummaryScreen()
    "Devices" -> DevicesScreen()
    "Alarm" -> AlarmScreen()
    "Fermenter" -> FermenterScreen()
    "Weather" -> WeatherScreen()
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  HomeRodeoTheme {
    Greeting("Android")
  }
}