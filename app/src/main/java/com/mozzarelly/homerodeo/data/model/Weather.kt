package com.mozzarelly.homerodeo.data.model

import com.mozzarelly.homerodeo.R
import com.mozzarelly.homerodeo.data.repo.DeviceAlias
import com.mozzarelly.homerodeo.data.repo.DeviceName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import kotlin.math.roundToInt

const val EmojiThermometer = "\uD83C\uDF21"
const val EmojiHouse = "\uD83C\uDFE0"
const val EmojiDrop = "\uD83D\uDCA7"
const val EmojiTarget = "\uD83C\uDFAF"
const val EmojiHeat = "\uD83D\uDD25"
const val EmojiCool = "\u2744\uFE0F" //"❄️"
const val EmojiFan = "\uD83D\uDCA8"
const val EmojiWind = "\uD83D\uDCA8"
const val EmojiRain = "\uD83C\uDF27"
const val EmojiSnow = "\uD83C\uDF28"
const val EmojiAway = "\uD83D\uDCA4"
const val EmojiLock = "\uD83D\uDD12"
const val EmojiLockKey = "\uD83D\uDD10"

/*
❄️
snowflake
Unicode: U+2744 U+FE0F, UTF-8: E2 9D 84 EF B8 8F
 */
@Serializable
data class Weather(@SerialName("inside") val inside: Inside? = null,
                   @SerialName("outside") val outside: Outside? = null
) {
    fun copyWithInside(away: Boolean? = null, temp: Temperature? = null, target: Temperature? = null, humidity: Float? = null, state: FurnaceState? = null,
                       on: Boolean? = null, mode: FurnaceMode? = null, security: SecurityMode? = null): Weather {
        val inside = inside ?: return this
        return copy(inside = inside.copy(
            away = away ?: inside.away,
            temp = temp ?: inside.temp,
            target = target ?: inside.target,
            humidity = humidity ?: inside.humidity,
            state = state ?: inside.state,
            on = on ?: inside.on,
            mode = mode ?: inside.mode,
            security = security ?: inside.security
        ))
    }

    val summary: String
        get() = "${inside?.summary ?: "?"}\n${outside?.summary ?: "?"}"

}

@Serializable
data class Inside(
    @SerialName("away") val away: Boolean,
    @SerialName("temp") val temp: Temperature,
    @SerialName("target") val target: Temperature,
    @SerialName("humidity") val humidity: Float,
    @SerialName("state") val state: FurnaceState,
    @SerialName("on") val on: Boolean,
    @SerialName("mode") val mode: FurnaceMode,
    @SerialName("security") val security: SecurityMode? = null,
    @SerialName("occupancy") val occupancy: String? = null,
    @SerialName("since") val since: String? = null
) : RodeoControl {
    override val alias: DeviceAlias get() = DeviceAlias("Thermostat")
    override val name get() = DeviceName("therm")
    override val description get() = name.string
    override val toggledOn = on
    override val reachable = true
    override val stateSubtext = "Set to $target°"
    override val stateText = "$furnaceState - $temp°"

    val securityIcon: String
        get() = when (security) {
            SecurityMode.ArmedHome -> EmojiLock
            SecurityMode.ArmedAway -> EmojiLockKey
            else -> ""
        }

    private val furnaceState: String?
        get() = when {
            state == FurnaceState.Heating -> EmojiHeat //"Heating"
            state == FurnaceState.Cooling -> EmojiCool //"Cooling"
            state == FurnaceState.Fan -> EmojiFan //"Fan on"
            state == FurnaceState.On -> "" //""On"
            mode == FurnaceMode.Eco -> EmojiAway //"Away"
            else -> null
        }

    val summary: String
        get() = listOfNotNull("$EmojiHouse $temp°", "$EmojiTarget $target°", furnaceState, securityIcon)
            .joinToString(" ")
}

@Serializable
data class Outside(
    @SerialName("temp") val temp: Temperature,
    @SerialName("feelsLike") val feelsLike: Temperature? = null,
    @SerialName("humidity") val humidity: Float,
    @SerialName("low") val low: Temperature,
    @SerialName("high") val high: Temperature,
    @SerialName("cond") val cond: String,
    @SerialName("windspeed") val windspeed: Float? = null,
    @SerialName("rain") val rain: Precip? = null,
    @SerialName("snow") val snow: Precip? = null,
    @SerialName("clouds") val clouds: Float? = null,
    @SerialName("airQuality") val airQuality: AirQuality? = null,
    @SerialName("night") val night: Boolean,
    @SerialName("forecast") val forecast: String? = null
){
    val conditionsFull: String
        get() = cond.toInitialCap() + (clouds?.let { "\ncloud coverage: ${it}%" } ?: "")

    val nextPrecip: String?
        get() = if (snow != null)
                    snow.oneHour?.let { "1hr: $it" } ?: snow.threeHour?.let { "3hr: $it" } ?: ""
                else if (rain != null)
                    rain.oneHour?.let { "1hr: $it" } ?: rain.threeHour?.let { "3hr: $it" } ?: ""
                else
                    null

    val windspeedRounded: String?
        get() = windspeed?.let { "${it.roundToInt()}mph" }

    val humidityRounded: String
        get() = "${humidity.roundToInt()}"

    private val tempNext: Pair<String, Temperature> = if (LocalDateTime.now().hour >= 16)
            "↓" to low
        else
            "↑" to high

    val conditions = cond.split(" ").takeIf { it.isNotEmpty() }

    val currConditions: List<String> = conditions?.let {
        val list = if (night) it + "night" else it
        if (clouds?.takeIf { it > 0f } != null)
            list + clouds.toString()
        else
            list
    } ?: emptyList()

    val aggConditions: List<String>
        get() {
            val air = airQuality?.desc?.takeIf { it != "good" }
            return if (air != null)
                listOf(air) + currConditions
            else
                currConditions
        }

    val summary: String
        get() = listOfNotNull(
//                    airQuality?.color?.takeIf { it != "green" }?.let { "Air: $it" },
                    "$EmojiThermometer${temp.rounded()}" + feelsLike?.rounded()?.let { "($it)" },
                    "${tempNext.first} ${tempNext.second.rounded()}",
//                    forecast?.toInitialCap() ?: cond.toInitialCap(),
//                    "$EmojiWind${windspeed.toInt()}mph".takeIf { windspeed > 10 },
                )
            .joinToString(" • ")

    val extras: String
        get() = listOfNotNull(
                    //"$EmojiWind$windspeedRounded".takeIf { windspeed.let { it != null && it > 3f } },
                    if (windspeed?.let { it > 3f } == true) " $windspeedRounded" else "",
                    "$EmojiRain $rain".takeIf { rain != null },
                    "$EmojiSnow $snow".takeIf { snow != null },
                    "$EmojiDrop$humidityRounded%",
                )
            .take(3)
            .joinToString(" • ")
}

@Serializable
data class Precip(
    @SerialName("1h") val oneHour: String? = null,
    @SerialName("3h") val threeHour: String? = null,
){
    override fun toString(): String = when {
        oneHour != null && threeHour != null -> "$oneHour (1hr), $threeHour (3hr)"
        threeHour != null -> "$threeHour (3hr)"
        oneHour != null -> "$oneHour"
        else -> "none"
    }
}

@Serializable
data class AirQuality(
    @SerialName("pm25") val pm25: String? = null,
    @SerialName("ozone") val ozone: String? = null,
    @SerialName("updated") val updated: String? = null,
    @SerialName("desc") val desc: String?,
){

    val color: String?
        get() = when (desc){
            null -> null
            "good" -> "green"
            "moderate" -> "yellow"
            "unhealthy for sensitive groups" -> "orange"
            "unhealthy" -> "RED!"
            "very unhealthy" -> "DON'T GO OUT!!"
            "hazardous" -> "DEATH!!!"
            else -> desc
        }

    val warningIcon: Int?
        get() = when (desc){
            "good" -> R.drawable.ic_air_green
            "moderate" -> R.drawable.ic_air_yellow
            "unhealthy for sensitive groups" -> R.drawable.ic_air_orange
            "unhealthy" -> R.drawable.ic_air_red
            "very unhealthy", "hazardous" -> R.drawable.ic_air_purple
            else -> null
        }

    val updatedTime: String?
        get() = updated?.replace(" AM", "am")?.replace(" PM", "pm")?.substringAfterLast(" ")

    override fun toString(): String {
        return "Air ${updatedTime?.let { "@$it" } ?: ""}\n" +
          "quality: ${color ?: "unknown"}\n" +
          "pm25: $pm25\n" +
          "ozone: $ozone"
    }
}

@Serializable
enum class SecurityMode(val key: String) {
    @SerialName("home") ArmedHome("home"),
    @SerialName("away") ArmedAway("away"),
    @SerialName("off") Disarmed("off");

    override fun toString(): String = key
}

@Serializable
enum class FurnaceMode {
    @SerialName("heat") Heat,
    @SerialName("cool") Cool,
    @SerialName("heat-cool") HeatCool,
    @SerialName("eco") Eco,
    @SerialName("off") Off
}

@Serializable
enum class FurnaceState {
    @SerialName("on") On,
    @SerialName("fan") Fan,
    @SerialName("heating") Heating,
    @SerialName("cooling") Cooling,
    @SerialName("off") Off
}

data class HouseState(val hvacMode: FurnaceMode, val hvacState: FurnaceState, val hvacAway: Boolean, val securityState: SecurityMode)
