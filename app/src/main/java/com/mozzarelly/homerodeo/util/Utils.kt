@file:Suppress("unused")

package com.mozzarelly.homerodeo.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mozzarelly.homerodeo.R
import com.mozzarelly.homerodeo.data.model.FurnaceMode
import com.mozzarelly.homerodeo.data.model.FurnaceState
import com.mozzarelly.homerodeo.data.model.Inside
import com.mozzarelly.homerodeo.data.model.Outside
import com.mozzarelly.homerodeo.data.model.Temperature
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.update as updateMutable

@Suppress("unused")
object Utils {


  fun Context.appWidgetManager() = AppWidgetManager.getInstance(this)

  fun Context.defaultPreferences(): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
  inline fun <reified T> Context.setPreference(key: String, value: T) { defaultPreferences()[key] = value }
  inline fun <reified T> Context.getPreference(key: String) = defaultPreferences().get<T>(key)
  inline fun <reified T> Context.getPreferenceOrInit(key: String, orElse: () -> T) = defaultPreferences().getOrInit(key, orElse)
  inline fun <reified T> Context.getPreferenceOrElse(key: String, orElse: () -> T) = defaultPreferences().getOrElse(key, orElse)

  inline operator fun <reified T> SharedPreferences.set(key: String, value: T?) {
    with (edit()){
      if (value == null)
        remove(key)
      else
        when (T::class) {
          String::class -> putString(key, value as String)
          Boolean::class -> putBoolean(key, value as Boolean)
          Float::class -> putFloat(key, value as Float)
          Int::class -> putInt(key, value as Int)
          Long::class -> putLong(key, value as Long)
          else -> throw IllegalArgumentException(T::class.simpleName)
        }

      commit()
    }
  }

  inline operator fun <reified T> SharedPreferences.get(key: String): T? {
    return if (contains(key)) {
      when (T::class) {
        String::class -> getString(key, null) as T
        Boolean::class -> getBoolean(key, false) as T
        Float::class -> getFloat(key, 0.0f) as T
        Int::class -> getInt(key, 0) as T
        Long::class -> getLong(key, 0L) as T
        else -> throw IllegalArgumentException(T::class.simpleName)
      }
    }
    else {
      null
    }
  }

  inline fun <reified T> SharedPreferences.getOrElse(key: String, orElse: () -> T): T {
    return if (contains(key)) {
      when (T::class) {
        String::class -> getString(key, null) as T
        Boolean::class -> getBoolean(key, false) as T
        Float::class -> getFloat(key, 0.0f) as T
        Int::class -> getInt(key, 0) as T
        Long::class -> getLong(key, 0L) as T
        else -> throw IllegalArgumentException(T::class.simpleName)
      }
    }
    else {
      orElse.invoke()
    }
  }

  inline fun <reified T> SharedPreferences.getOrInit(key: String, orElse: () -> T): T {
    return if (contains(key)) {
      when (T::class) {
        String::class -> getString(key, null) as T
        Boolean::class -> getBoolean(key, false) as T
        Float::class -> getFloat(key, 0.0f) as T
        Int::class -> getInt(key, 0) as T
        Long::class -> getLong(key, 0L) as T
        else -> throw IllegalArgumentException(T::class.simpleName)
      }
    }
    else {
      val value = orElse.invoke()
      Log.i("READER", "BeginDay was not found in prefs; adding $value.")
      with (edit()){
        when (value){
          is String -> putString(key, value)
          is Boolean -> putBoolean(key, value)
          is Float -> putFloat(key, value)
          is Int -> putInt(key, value)
          is Long -> putLong(key, value)
          else -> throw IllegalArgumentException(value.toString())
        }
      }.commit()

      value
    }
  }

  inline fun Switch.setOnStateChanged(crossinline listener: (Boolean) -> Unit){
    setOnCheckedChangeListener { button, state ->
      if (button.isPressed)
        listener(state)
    }
  }

}

fun <V: View> View.configure(id: Int, block: V.() -> Unit){
  block.invoke(findViewById(id))
}

fun <V: View> V.setOnClickListener(view: Int, block: (View) -> Unit){
  findViewById<View>(view).setOnClickListener(block)
}

fun View.setOnToggleListener(view: Int, block: (Boolean) -> Unit) {
  findViewById<Switch>(view).setOnCheckedChangeListener { _, on ->
    block(on)
  }
}

fun String?.visibility() = if (this != null)
  View.VISIBLE
else
  View.GONE

fun Context.showLongToast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(text: String) {
  Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

//fun Context.scheduleService(
//  alarmTime: Long,
//  serviceClass: Class<AlarmWidgetRefreshService>,
//  requestCode: Int
//) {
//  (getSystemService(Context.ALARM_SERVICE) as AlarmManager).set(
//    AlarmManager.RTC, alarmTime,
//    PendingIntent.getService(
//      this, requestCode,
//      Intent(this, serviceClass).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
//      PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//    )
//  )
//}

private fun getTempIcon(temp: Temperature): Int? = when (temp.temp?.roundToInt()){
  0 -> R.drawable.temp0
  1 -> R.drawable.temp1
  2 -> R.drawable.temp2
  3 -> R.drawable.temp3
  4 -> R.drawable.temp4
  5 -> R.drawable.temp5
  6 -> R.drawable.temp6
  7 -> R.drawable.temp7
  8 -> R.drawable.temp8
  9 -> R.drawable.temp9
  10 -> R.drawable.temp10
  11 -> R.drawable.temp11
  12 -> R.drawable.temp12
  13 -> R.drawable.temp13
  14 -> R.drawable.temp14
  15 -> R.drawable.temp15
  16 -> R.drawable.temp16
  17 -> R.drawable.temp17
  18 -> R.drawable.temp18
  19 -> R.drawable.temp19
  20 -> R.drawable.temp20
  21 -> R.drawable.temp21
  22 -> R.drawable.temp22
  23 -> R.drawable.temp23
  24 -> R.drawable.temp24
  25 -> R.drawable.temp25
  26 -> R.drawable.temp26
  27 -> R.drawable.temp27
  28 -> R.drawable.temp28
  29 -> R.drawable.temp29
  30 -> R.drawable.temp30
  31 -> R.drawable.temp31
  32 -> R.drawable.temp32
  33 -> R.drawable.temp33
  34 -> R.drawable.temp34
  35 -> R.drawable.temp35
  36 -> R.drawable.temp36
  37 -> R.drawable.temp37
  38 -> R.drawable.temp38
  39 -> R.drawable.temp39
  40 -> R.drawable.temp40
  41 -> R.drawable.temp41
  42 -> R.drawable.temp42
  43 -> R.drawable.temp43
  44 -> R.drawable.temp44
  45 -> R.drawable.temp45
  46 -> R.drawable.temp46
  47 -> R.drawable.temp47
  48 -> R.drawable.temp48
  49 -> R.drawable.temp49
  50 -> R.drawable.temp50
  51 -> R.drawable.temp51
  52 -> R.drawable.temp52
  53 -> R.drawable.temp53
  54 -> R.drawable.temp54
  55 -> R.drawable.temp55
  56 -> R.drawable.temp56
  57 -> R.drawable.temp57
  58 -> R.drawable.temp58
  59 -> R.drawable.temp59
  60 -> R.drawable.temp60
  61 -> R.drawable.temp61
  62 -> R.drawable.temp62
  63 -> R.drawable.temp63
  64 -> R.drawable.temp64
  65 -> R.drawable.temp65
  66 -> R.drawable.temp66
  67 -> R.drawable.temp67
  68 -> R.drawable.temp68
  69 -> R.drawable.temp69
  70 -> R.drawable.temp70
  71 -> R.drawable.temp71
  72 -> R.drawable.temp72
  73 -> R.drawable.temp73
  74 -> R.drawable.temp74
  75 -> R.drawable.temp75
  76 -> R.drawable.temp76
  77 -> R.drawable.temp77
  78 -> R.drawable.temp78
  79 -> R.drawable.temp79
  80 -> R.drawable.temp80
  81 -> R.drawable.temp81
  82 -> R.drawable.temp82
  83 -> R.drawable.temp83
  84 -> R.drawable.temp84
  85 -> R.drawable.temp85
  86 -> R.drawable.temp86
  87 -> R.drawable.temp87
  88 -> R.drawable.temp88
  89 -> R.drawable.temp89
  90 -> R.drawable.temp90
  91 -> R.drawable.temp91
  92 -> R.drawable.temp92
  93 -> R.drawable.temp93
  94 -> R.drawable.temp94
  95 -> R.drawable.temp95
  96 -> R.drawable.temp96
  97 -> R.drawable.temp97
  98 -> R.drawable.temp98
  99 -> R.drawable.temp99
  100 -> R.drawable.temp100
  else -> null
}

fun Outside.getIcon(useTempIcons: Boolean) = if (useTempIcons)
  getTempIcon(temp) ?: getOutsideIcon(aggConditions)
else
  getOutsideIcon(aggConditions)

@DrawableRes
fun getOutsideIcon(conds: List<String>): Int {
  val night = conds.contains("night")

  for (name in conds) {
    val found = when (name) {
      "rain", "rainy" -> if (night) R.drawable.nt_rain else R.drawable.rain
      "snowy", "snow" -> if (night) R.drawable.nt_snow else R.drawable.snow
      "hail" -> R.drawable.hail
      "fog", "foggy", "mist", "dust", "haze" -> if (night) R.drawable.nt_fog else R.drawable.fog
      "cloudy", "cloud", "clouds" -> {
        val mostly = conds.contains("mostly") ||
            conds.find {
              it.replace("%", "")
                .matches(Regex("^[0-9.]+]%?$"))
            }
              ?.let { it.toFloat() > 50.0 } ?: false

        when {
          mostly && !night -> R.drawable.mostlycloudy
          mostly && night -> R.drawable.nt_mostlycloudy
          !mostly && night -> R.drawable.nt_partlycloudy
          else -> R.drawable.partlycloudy
        }
      }
      "windy", "wind" -> R.drawable.wind
      "sun", "sunny", "clear" -> if (night) R.drawable.nt_clear else R.drawable.clear
      "storm", "storms", "thunderstorm", "thunderstorms" -> if (night) R.drawable.nt_tstorms else R.drawable.tstorms
      else -> null
    }

    if (found != null)
      return found
  }

  return R.drawable.blank.also {
    println("No weather icon found for ${conds.joinToString(",")}")
  }
}

fun TextView.setTextOrHide(text: String?) {
  if (!text.isNullOrEmpty()) {
    visibility = View.VISIBLE
    setText(text)
  }
  else {
    visibility = View.GONE
  }
}

fun ImageView.setIcon(house: Inside?) {
  house?.icon()?.let { setImageResource(it) }
}

@DrawableRes
fun Inside.icon() =
  if (occupancy.isNullOrEmpty()) {
    when (mode to state) {
      FurnaceMode.Heat to FurnaceState.On -> R.drawable.house_heating_away
      FurnaceMode.Heat to FurnaceState.Fan -> R.drawable.house_fan_away

      FurnaceMode.HeatCool to FurnaceState.Cooling -> R.drawable.house_cooling_away
      FurnaceMode.HeatCool to FurnaceState.Heating -> R.drawable.house_heating_away
      FurnaceMode.HeatCool to FurnaceState.Fan -> R.drawable.house_fan_away

      FurnaceMode.Cool to FurnaceState.On -> R.drawable.house_cooling_away
      FurnaceMode.Cool to FurnaceState.Fan -> R.drawable.house_fan_away

      FurnaceMode.Off to FurnaceState.Fan -> R.drawable.house_fan_away
      FurnaceMode.Off to FurnaceState.Off -> R.drawable.house_away

      else -> R.drawable.house_away
    }
  }
  else {
    when (mode to state) {
      FurnaceMode.Heat to FurnaceState.On -> R.drawable.house_heating_home
      FurnaceMode.Heat to FurnaceState.Fan -> R.drawable.house_fan_home

      FurnaceMode.HeatCool to FurnaceState.Heating -> R.drawable.house_heating_home
      FurnaceMode.HeatCool to FurnaceState.Cooling -> R.drawable.house_cooling_home
      FurnaceMode.HeatCool to FurnaceState.Fan -> R.drawable.house_fan_home

      FurnaceMode.Cool to FurnaceState.On -> R.drawable.house_cooling_home
      FurnaceMode.Cool to FurnaceState.Fan -> R.drawable.house_fan_home

      FurnaceMode.Off to FurnaceState.Fan -> R.drawable.house_fan_home
      FurnaceMode.Off to FurnaceState.Off -> R.drawable.house_home

      else -> R.drawable.house_home
    }
  }

inline operator fun <reified T> SharedPreferences.set(key: String, value: T?) {
  with(edit()) {
    if (value == null)
      remove(key)
    else
      when (T::class) {
        String::class -> putString(key, value as String)
        Boolean::class -> putBoolean(key, value as Boolean)
        Float::class -> putFloat(key, value as Float)
        Int::class -> putInt(key, value as Int)
        Long::class -> putLong(key, value as Long)
        else -> throw IllegalArgumentException(T::class.simpleName)
      }

    commit()
  }
}

fun Throwable.rethrowIfCancellation() {
  if (this is CancellationException)
    throw this
}

fun <T> StateFlow<T>.mutable() = this as MutableStateFlow<T>
fun <T> SharedFlow<T>.mutable() = this as MutableSharedFlow<T>

fun <T : Any> intentExtra(defaultValue: T? = null) = IntentExtraDelegate(defaultValue)
fun <T : Any?> optionalIntentExtra() = OptionalIntentExtraDelegate<T>()

/**
 * Copied from FragmentArgumentDelegate with modifications.
 *
 * @param T property type
 */
class IntentExtraDelegate<T : Any>(private val defaultValue: T? = null) : ReadOnlyProperty<Activity, T> {

  override operator fun getValue(thisRef: Activity, property: KProperty<*>): T {
    val propName = property.name

    if (defaultValue == null) {
      val extras = thisRef.intent.extras
        ?: throw IllegalStateException("Cannot read intent extra $propName: no extras have been set")

      if (!extras.containsKey(propName))
        throw IllegalStateException("Intent extra '$propName' is missing")

      @Suppress("UNCHECKED_CAST")
      return extras.get(propName) as T
    }
    else {
      val extras = thisRef.intent.extras ?: return defaultValue
      if (!extras.containsKey(propName))
        return defaultValue

      @Suppress("UNCHECKED_CAST")
      return extras.get(propName) as T
    }
  }

}

class OptionalIntentExtraDelegate<T : Any?> {
  operator fun getValue(thisRef: Activity, property: KProperty<*>): T? {
    val propName = property.name

    val extras = thisRef.intent.extras ?: return null
    if (!extras.containsKey(propName))
      return null

    @Suppress("UNCHECKED_CAST")
    return extras.get(propName) as T
  }

}

inline fun <T> StateFlow<T>.update(func: () -> T){
  if (this !is MutableStateFlow)
    error("This flow is not mutable! Cannot update a ${this::class.java}")

  updateMutable { func() }
}

inline fun <T> StateFlow<T>.updateCopying(func: (T) -> T){
  if (this !is MutableStateFlow)
    error("This flow is not mutable! Cannot update a ${this::class.java}")

  updateMutable { func(value) }
}

fun Temperature?.isValid() = this != null && this != Temperature.Off

fun String.to12HourTime() = split(":").takeIf { it.size == 2 }?.let {
  val hour = it[0].toInt()
  val min = it[1].padStart(length = 2, padChar = '0')
  val ampm = if (hour > 11) "pm" else "am"
  val newHour = (hour % 12).let { if (it == 0) 12 else it }
  "$newHour:$min$ampm"
} ?: this

fun <T> T?.notNullAnd(predicate: (T) -> Boolean): Boolean = this != null && predicate(this)


@Composable
fun FullCenteredRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit){
  Row(
    modifier = modifier
      .fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
  ){
    content()
  }
}

@SuppressLint("ComposableNaming")
@Composable
fun <T> Flow<T>.collectAsEffect(
  block: (T) -> Unit
) {
  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(this, lifecycleOwner.lifecycle) {
    lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
      this@collectAsEffect.collectLatest { block.invoke(it) }
    }
  }
}

fun CoroutineScope.launchSafe(onError: (Throwable) -> Unit, block: suspend CoroutineScope.() -> Unit) {
  launch {
    try {
      block()
    }
    catch (t: Throwable) {
      t.rethrowIfCancellation()
      onError(t)
    }
  }
}

inline fun Modifier.applyIf(condition: Boolean, effect: Modifier.() -> Modifier) = if (condition) effect() else this

fun NavController.navigateAsTop(route: String) = navigate(route){
  // Pop up to the start destination of the graph to avoid building up a large stack of destinations on the back stack as users select items
  popUpTo(graph.findStartDestination().id) {
    saveState = true
  }

  // Avoid multiple copies of the same destination when reselecting the same item
  launchSingleTop = true

  // Restore state when reselecting a previously selected item
  restoreState = true
}