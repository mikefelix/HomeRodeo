package com.mozzarelly.homerodeo.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mozzarelly.homerodeo.R
import com.mozzarelly.homerodeo.data.model.Device
import com.mozzarelly.homerodeo.data.repo.DeviceAlias
import com.mozzarelly.homerodeo.ui.vm.DeviceActions
import com.mozzarelly.homerodeo.ui.vm.DeviceUiState
import com.mozzarelly.homerodeo.ui.vm.DevicesViewModel
import com.mozzarelly.homerodeo.util.FullCenteredRow
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.collectAsMutableState
import com.mozzarelly.homerodeo.util.composables.BodyText
import com.mozzarelly.homerodeo.util.composables.FullSizeUiWithSheet
import com.mozzarelly.homerodeo.util.composables.LocalImage
import com.mozzarelly.homerodeo.util.composables.NumberPicker
import com.mozzarelly.homerodeo.util.composables.PrimaryButton
import com.mozzarelly.homerodeo.util.composables.SecondaryButton
import com.mozzarelly.homerodeo.util.composables.SubtitleText
import com.mozzarelly.homerodeo.util.composables.TitleText
import com.mozzarelly.homerodeo.util.composables.Ui
import kotlinx.coroutines.flow.StateFlow

typealias DeviceUiFlow = StateFlow<UiState<Device>>

@Composable
fun DevicesScreen(
  viewModel: DevicesViewModel = hiltViewModel(),
  @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
) {
  val state by viewModel.state.collectAsState()
  val sheetState = viewModel.sheetState.collectAsMutableState()

  DevicesScreen(
    state = state,
    sheetState = sheetState,
    modifier = modifier,
    actions = viewModel
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
  state: UiState<List<DeviceUiState>>,
  sheetState: MutableState<DeviceUiFlow?>,
  @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
  actions: DeviceActions
) {
  FullSizeUiWithSheet(
    state = state,
    onRetry = { },
    sheetState = sheetState,
    sheetContent = {
      DevicesBottomSheetUi(
        deviceFlow = it,
        favorites = emptyList(),
        toggleFavorite = {},
        hideSheet = actions::hideSheet,
        onRetry = {
          it.value.state?.let {
            actions.onRetryDevice(it.alias)
          }
        },
        onToggle = actions::onToggleDevice
      )
    }
  ) { devices ->
    DevicesList(
      devices = devices,
      modifier = modifier,
      header = {},
      onToggleDevice = actions::onToggleDevice,
      onRetryDevice = actions::onRetryDevice,
      onViewDevice = { dev ->
        actions.showSheet(devices.first { it.first == dev }.second)
      }
    )
  }
}

@Composable
fun DevicesList(
  devices: List<Pair<DeviceAlias, DeviceUiFlow>>,
  modifier: Modifier = Modifier,
  header: @Composable () -> Unit,
  onRetryDevice: (DeviceAlias) -> Unit,
  onViewDevice: (DeviceAlias) -> Unit,
  onToggleDevice: (Device, Boolean?, Boolean, Int?) -> Unit,
){
  LazyColumn(modifier = modifier
    .fillMaxWidth()
  ) {
    item {
      header()
    }

    items(devices) { (name, flow) ->
      DeviceListItem(
        deviceAlias = name,
        deviceFlow = flow,
        modifier = Modifier
          .padding(horizontal = 16.dp),
        onView = onViewDevice,
        onRetry = onRetryDevice,
        onToggle = { dev, state -> onToggleDevice(dev, state, false, null) })

      HorizontalDivider()
    }
  }
}


@Composable
fun DeviceListItem(
  deviceAlias: DeviceAlias,
  deviceFlow: DeviceUiFlow,
  modifier: Modifier = Modifier,
  onRetry: (DeviceAlias) -> Unit,
  onView: (DeviceAlias) -> Unit,
  onToggle: (Device, Boolean?) -> Unit
) {
  val state by deviceFlow.collectAsState()

  LaunchedEffect("loadOnDisplay"){
    if (deviceFlow.value.state == null) {
      onRetry(deviceAlias)
    }
  }

  Ui(state,
    onRetry = {
      onRetry(deviceAlias)
    },
    loadingContent = {
      DeviceRow(
        deviceAlias = deviceAlias,
        device = null,
        modifier = modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp)
          .height(60.dp),
        onView = onView,
        onToggle = onToggle
      )
    },
    errorContent = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .height(60.dp)
          .padding(start = 8.dp)
      ) {
        TitleText(
          text = "Error loading ${deviceAlias.string}!",
          align = TextAlign.Center,
          modifier = Modifier
        )
      }
    }
  ) { device ->
    DeviceRow(
      deviceAlias = deviceAlias,
      device = device,
      modifier = Modifier
        .fillMaxWidth()
        .height(60.dp),
      onView = onView,
      onToggle = onToggle
    )
  }
}

@Composable
fun DeviceRow(
  deviceAlias: DeviceAlias,
  device: Device?,
  modifier: Modifier,
  onView: (DeviceAlias) -> Unit,
  onToggle: (Device, Boolean?) -> Unit
){
  Row(
    modifier = modifier
      .clickable { onView(device?.alias ?: deviceAlias) },
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Icon(
      modifier = Modifier
        .size(24.dp),
      tint = MaterialTheme.colorScheme.onBackground,
      painter = painterResource(device?.iconForType?.first ?: R.drawable.blank),
      contentDescription = "device",
    )

    SubtitleText((device?.alias ?: deviceAlias).string, modifier = Modifier.padding(start = 8.dp))

    Spacer(modifier = Modifier.weight(1f))

    if (device == null || device.incomplete) {
      CircularProgressIndicator(modifier = Modifier.size(36.dp))
    }
    else {
      Switch(
        modifier = Modifier.scale(1.1f),
        checked = device.isOn(),
        colors = SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.onBackground,
          uncheckedThumbColor = MaterialTheme.colorScheme.onBackground,
          checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
          uncheckedTrackColor = MaterialTheme.colorScheme.primaryContainer,
          checkedBorderColor = MaterialTheme.colorScheme.background,
          uncheckedBorderColor = MaterialTheme.colorScheme.background,
        ),
        onCheckedChange = { onToggle(device, it) },
        thumbContent = {
          if (device.overridden){
            Icon(
              painter = painterResource(R.drawable.ic_lock),
              contentDescription = "lock",
              modifier = Modifier.size(16.dp)
            )
          }
        }
      )
    }
  }
}

@Composable
fun DevicesBottomSheetUi(
  deviceFlow: DeviceUiFlow,
  favorites: List<DeviceAlias>,
  onRetry: () -> Unit,
  hideSheet: () -> Unit,
  toggleFavorite: (DeviceAlias) -> Unit,
  onToggle: (device: Device, on: Boolean, lock: Boolean, timer: Int?) -> Unit,
) {
  val deviceState by deviceFlow.collectAsState()
  var overriding by remember { mutableStateOf(false) }
  var setTimer by remember { mutableStateOf(false) }
  val timer = remember { mutableStateOf<Float?>(null) }

  Ui(deviceState, onRetry = onRetry) { device ->
    val deviceIsFavorite = device.alias in favorites
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.padding(8.dp)
    ) {
      FullCenteredRow {
        LocalImage(device.iconForType.first, "icon", modifier = Modifier.size(32.dp))

        Text(
          text = device.desc(),
          modifier = Modifier
            .padding(horizontal = 16.dp),
          textAlign = TextAlign.Center,
          fontFamily = FontFamily.SansSerif,
          fontSize = 30.sp,
          color = MaterialTheme.colorScheme.onBackground,
        )
      }

      device.schedule?.let { schedule ->
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
          schedule.history?.let { BodyText("History: $it") }
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
          schedule.on?.let { BodyText("On: $it") }
        }
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
          schedule.off?.let { BodyText("Off: $it") }
        }
      }

      device.timer?.let {
        Row(modifier = Modifier.padding(horizontal = 16.dp)) {
          BodyText("Turning ${if (it.first) "on" else "off"} at ${it.second}.")
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
      ) {
        Checkbox(overriding, onCheckedChange = { overriding = it })
        BodyText("Lock", align = TextAlign.Center, modifier = Modifier.clickable {
          overriding = !overriding
        })

        Spacer(modifier = Modifier.padding(8.dp))

        Checkbox(setTimer, onCheckedChange = { setTimer = it })
        BodyText("Set timer", align = TextAlign.Center, modifier = Modifier.clickable {
          setTimer = !setTimer
        })
      }

      if (setTimer) {
        Row {
          NumberPicker(state = timer, allowDecimal = false, start = 1f, modifier = Modifier.fillMaxWidth())
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
      ) {
        if (device.isOn()) {
          SecondaryButton("Off") {
            onToggle(device, false, overriding, timer.value.takeUnless { it == null || it <= 0 }?.toInt())
            hideSheet()
          }

          PrimaryButton("On") {
            onToggle(device, true, overriding, timer.value.takeUnless { it == null || it <= 0 }?.toInt())
            hideSheet()
          }
        }
        else {
          PrimaryButton("Off") {
            onToggle(device, false, overriding, timer.value.takeUnless { it == null || it <= 0 }?.toInt())
            hideSheet()
          }

          SecondaryButton("On") {
            onToggle(device, true, overriding, timer.value.takeUnless { it == null || it <= 0 }?.toInt())
            hideSheet()
          }
        }
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp)
      ) {
        Checkbox(deviceIsFavorite, onCheckedChange = { toggleFavorite(device.alias) })
        BodyText("Favorite", align = TextAlign.Center, modifier = Modifier.clickable {
          setTimer = !setTimer
        })
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

