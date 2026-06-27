package com.mozzarelly.homerodeo.ui.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.data.model.Time
import com.mozzarelly.homerodeo.data.repo.AlarmRepository
import com.mozzarelly.homerodeo.data.repo.DeviceName
import com.mozzarelly.homerodeo.data.repo.DevicesRepository
import com.mozzarelly.homerodeo.ui.util.ViewModelWithBottomSheet
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.map
import com.mozzarelly.homerodeo.util.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

interface AlarmActions {
  fun editDay(day: Day)
  fun dismissEdit()
  fun setTime(day: Day, time: Time?, saveAsSetting: Boolean)
  fun disableToday()
}

@HiltViewModel
class AlarmViewModel @Inject constructor(
  private val repo: AlarmRepository,
  private val deviceRepo: DevicesRepository,
  private val savedStateHandle: SavedStateHandle
) : ViewModelWithBottomSheet<Day>(), AlarmActions {

  private var disabledForDay: Long? = null

  val state: StateFlow<UiState<AlarmData>> = repo.alarmFlow.map { it.toUiState() }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), UiState.loading())

  override fun editDay(day: Day) {
    showSheet(day)
  }

  override fun dismissEdit() {
    hideSheet()
  }

  override fun setTime(day: Day, time: Time?, saveAsSetting: Boolean) {
    viewModelScope.launch {
      repo.saveDay(day.num.toString(), time?.toString() ?: "off", saveAsSetting)
    }
  }

  override fun disableToday() {
    state.value.state?.let {
      disabledForDay = currentDay()
      setTime(it.next, null, false)

      viewModelScope.launch {
        deviceRepo.deviceOffByName(DeviceName("bedheat"), false, null)
      }
    }
  }

  //  override suspend fun loadState(): UiState<Alarm> = repo.getAlarm().toUiState()
//  override fun refresh(source: String, showProgress: Boolean) {
//    viewModelScope.launch {
//      repo.refresh()
//    }
//  }
}

fun currentDay() = System.currentTimeMillis() / 1000 / 60 / 60 / 24
