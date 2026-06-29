package com.mozzarelly.homerodeo

import android.app.Application
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeRodeoApplication : Application() {
  private val useRealApiInTest = false

  val useFakes = when {
    useRealApiInTest -> false // Usually you want real API in real tests
    BuildConfig.API_AUTH.isBlank() || BuildConfig.BASE_URL.isBlank() -> true
    isEmulator() -> true
    else -> false
  }

  private fun isEmulator(): Boolean {
    return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        || Build.FINGERPRINT.startsWith("generic")
        || Build.FINGERPRINT.startsWith("unknown")
        || Build.HARDWARE.contains("goldfish")
        || Build.HARDWARE.contains("ranchu")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.PRODUCT.contains("sdk_google")
        || Build.PRODUCT.contains("google_sdk")
        || Build.PRODUCT.contains("sdk")
        || Build.PRODUCT.contains("sdk_x86")
        || Build.PRODUCT.contains("vbox86p")
        || Build.PRODUCT.contains("emulator")
        || Build.PRODUCT.contains("simulator")
  }
}