package com.mozzarelly.homerodeo.ui.vm

import androidx.lifecycle.viewModelScope
import com.mozzarelly.homerodeo.data.model.Device
import com.mozzarelly.homerodeo.data.repo.DeviceAlias
import com.mozzarelly.homerodeo.data.repo.DevicesRepository
import com.mozzarelly.homerodeo.ui.screens.DeviceUiFlow
import com.mozzarelly.homerodeo.ui.util.ScreenWithBottomSheetActions
import com.mozzarelly.homerodeo.ui.util.ViewModelWithBottomSheet
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.launchSafe
import com.mozzarelly.homerodeo.util.toUiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

typealias DeviceUiState = Pair<DeviceAlias, StateFlow<UiState<Device>>>

interface DeviceActions : ScreenWithBottomSheetActions<DeviceUiFlow> {
  fun onToggleDevice(device: Device, on: Boolean?, lock: Boolean, timer: Int?)
  fun onRetryDevice(deviceAlias: DeviceAlias)
}

@HiltViewModel
class DevicesViewModel @Inject constructor(
  private val devicesRepository: DevicesRepository
) : ViewModelWithBottomSheet<DeviceUiFlow>(), DeviceActions {

  val flows: List<DeviceUiState> = devicesRepository.allFlows.map {
    it.first to it.second.toUiStateFlow(viewModelScope)
  }

  val state: MutableStateFlow<UiState<List<DeviceUiState>>> = MutableStateFlow(UiState(flows))

  override fun onToggleDevice(
    device: Device,
    on: Boolean?,
    lock: Boolean,
    timer: Int?
  ) {
    viewModelScope.launchSafe(onError = ::handleError) {
      devicesRepository.toggleDevice(device.alias)
    }
  }

  override fun onRetryDevice(deviceAlias: DeviceAlias) {
    viewModelScope.launchSafe(onError = ::handleError) {
      devicesRepository.refresh(deviceAlias, "retry")
    }
  }

  private fun handleError(t: Throwable) {
    // TODO
    throw t
  }
}