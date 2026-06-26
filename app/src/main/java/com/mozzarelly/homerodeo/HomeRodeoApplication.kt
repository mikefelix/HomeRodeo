package com.mozzarelly.homerodeo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeRodeoApplication : Application() {
  private val forceFakes = true

  val useFakes = forceFakes || BuildConfig.API_AUTH.isBlank()
}
