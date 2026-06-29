package com.mozzarelly.homerodeo.ui.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.data.model.Day
import com.mozzarelly.homerodeo.data.model.Time
import com.mozzarelly.homerodeo.data.repo.AlarmRepository
import com.mozzarelly.homerodeo.data.repo.DevicesRepository
import com.mozzarelly.homerodeo.ui.util.ViewModelWithBottomSheet
import com.mozzarelly.homerodeo.util.UiState
import com.mozzarelly.homerodeo.util.launchSafe
import com.mozzarelly.homerodeo.util.mutable
import com.mozzarelly.homerodeo.util.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface AlarmActions {
  fun editDay(day: Day)
  fun dismissEdit()
  fun setTime(day: Day, time: Time?, saveAsSetting: Boolean)
  fun disableToday()
  fun turnOff()
}

@HiltViewModel
class AlarmViewModel @Inject constructor(
  private val repo: AlarmRepository,
  private val deviceRepo: DevicesRepository,
  private val savedStateHandle: SavedStateHandle
) : ViewModelWithBottomSheet<Day>(), AlarmActions {

  private var disabledForDay: Long? = null

  val state: StateFlow<UiState<AlarmData>> = MutableStateFlow(UiState.loading())

  init {
    viewModelScope.launchSafe(onError = ::handleError) {
      state.mutable().value = repo.getAlarm().toUiState()
    }
  }

  override fun editDay(day: Day) {
    showSheet(day)
  }

  override fun dismissEdit() {
    hideSheet()
  }

  override fun setTime(day: Day, time: Time?, saveAsSetting: Boolean) {
    viewModelScope.launchSafe(onError = ::handleError) {
      val newState = repo.saveDay(day.num.toString(), time?.toString() ?: "off", saveAsSetting)
      state.mutable().value = newState.toUiState()
    }
  }

  override fun disableToday() {
    state.value.state?.let {
      disabledForDay = currentDay()
      setTime(it.next, null, false)
    }
  }

  override fun turnOff() {
    viewModelScope.launchSafe(onError = ::handleError) {
      val newState = repo.turnOffRinging()
      state.mutable().value = newState.toUiState()
    }
  }

  private fun handleError(e: Throwable) {
    state.mutable().value = UiState.error(e)
  }
}

fun currentDay() = System.currentTimeMillis() / 1000 / 60 / 60 / 24
