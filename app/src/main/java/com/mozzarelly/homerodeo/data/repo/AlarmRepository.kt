package com.mozzarelly.homerodeo.data.repo

import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.data.api.AlarmApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
  private val api: AlarmApi
) {
  val currentState = MutableStateFlow<ApiResponse<AlarmData>?>(null)

  val alarmFlow = flow {
    refresh()
    currentState.collect {
      if (it != null)
        emit(it)
    }
  }

  private suspend fun getAlarm(): ApiResponse<AlarmData> = api.getAlarm()

  suspend fun saveDay(day: String, setting: String, saveAsSetting: Boolean) = (if (saveAsSetting)
      api.saveSetting(day, setting)
    else
      api.saveOverride(day, setting)
  )
    .also { refresh() }

  suspend fun refresh(){
    currentState.emit(getAlarm())
  }
}
