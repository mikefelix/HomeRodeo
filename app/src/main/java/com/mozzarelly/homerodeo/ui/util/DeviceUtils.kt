package com.mozzarelly.homerodeo.ui.util

import com.mozzarelly.homerodeo.R

private infix fun String.foundIn(str: String) = Regex(this, RegexOption.IGNORE_CASE).containsMatchIn(str)

typealias DeviceIcon = Pair<Int, Int>

fun drawableForType(name: String): DeviceIcon = when {
  "(fan|vent)" foundIn name -> R.drawable.ic_breeze to 10
  "(lamp|dining)" foundIn name -> R.drawable.ic_lamp to 0
  "bed" foundIn name -> R.drawable.ic_bed to 10
  "heat" foundIn name -> R.drawable.ic_thermometer to 10
  "coffee" foundIn name -> R.drawable.ic_coffee to 5
  "garage" foundIn name -> R.drawable.ic_car to 10
  "charger" foundIn name -> R.drawable.ic_battery to 0
  "stereo" foundIn name -> R.drawable.ic_speaker to 10
  "grow" foundIn name -> R.drawable.ic_leaf to 5
  "piano" foundIn name -> R.drawable.ic_power_button to 5
  "computer" foundIn name -> R.drawable.ic_computer to 5
  "(security|lock)" foundIn name -> R.drawable.ic_lock to 5
  "(wine|aquarium|betta|outside|color|light)" foundIn name -> R.drawable.ic_bulb to 0
  "(phone|mike|lily|aaron|kim)" foundIn name -> R.drawable.ic_smartphone to 5
  else -> R.drawable.ic_power_button to 0
}

fun drawableForLocation(name: String): DeviceIcon = when {
  "bed" foundIn name -> R.drawable.ic_bed to 10
  "coffee" foundIn name -> R.drawable.ic_coffee to 5
  "garage" foundIn name -> R.drawable.ic_car to 10
  "charger" foundIn name -> R.drawable.ic_battery to 0
  "stereo" foundIn name -> R.drawable.ic_speaker to 10
  "grow" foundIn name -> R.drawable.ic_leaf to 5
  "piano" foundIn name -> R.drawable.ic_power_button to 5
  "computer" foundIn name -> R.drawable.ic_computer to 5
  "(fan|vent)" foundIn name -> R.drawable.ic_breeze to 10
  "lamp" foundIn name -> R.drawable.ic_lamp to 0
  "(phone|mike|lily|aaron|kim)" foundIn name -> R.drawable.ic_smartphone to 5
  else -> R.drawable.ic_bulb to 0
}

