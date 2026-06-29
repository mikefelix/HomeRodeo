package com.mozzarelly.homerodeo.data.repo

import com.mozzarelly.homerodeo.data.api.AlarmApi
import com.mozzarelly.homerodeo.data.model.AlarmData
import com.mozzarelly.homerodeo.util.ApiResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmRepository @Inject constructor(
  private val api: AlarmApi
) {
  suspend fun getAlarm(): ApiResponse<AlarmData> = api.getAlarm()

  suspend fun saveDay(day: String, setting: String, saveAsSetting: Boolean): ApiResponse<AlarmData> {
    if (saveAsSetting)
      api.saveSetting(day, setting)
    else
      api.saveOverride(day, setting)

    return getAlarm()
  }

  suspend fun turnOffRinging(): ApiResponse<AlarmData> {
    api.turnOff()
    return getAlarm()
  }
}
