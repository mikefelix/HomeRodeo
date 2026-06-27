package com.mozzarelly.homerodeo.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Calendar

/*
{
    "on": false,
    "time": "07:30",
    "next": {
        "num": 9,
        "index": 2,
        "time": "7:30",
        "auto": false,
        "overridden": false
    },
    "lastTriggered": {
        "day": "20190924",
        "time": "07:30",
        "action": "rung",
        "today": true
    },
    "days": [{
        "time": "7:30",
        "type": "f",
        "num": 7,
        "name": "monday_even",
        "auto": false
    }, ...
    ],
    "today": {
        "num": 8,
        "index": 1,
        "type": "f",
        "name": "tuesday_even",
        "time": "07:30",
        "hasTriggered": true
    }
}
 */

@Serializable
data class AlarmData(
    @SerialName("on") val on: Boolean,
    @SerialName("nextNum") val nextNum: Int,
    @SerialName("override") val override: AlarmOverride? = null,
    @SerialName("lastTriggered") val lastTriggered: Triggered? = null,
    @SerialName("days") val days: List<Day>,
    @SerialName("today") val today: Day,
    @Transient val disabledToday: Boolean = false,
){

  val allowDisableToday: Boolean
      get() = !disabledToday && next == today && status.endsWith("ringing")

  val next by lazy { days.find { it.num == nextNum } ?: throw RuntimeException("Can't find day $nextNum") }

    private val nextDayName = next.desc(inReferenceTo = today)

    val nextTime = next.time.toString().toInitialCap()

    val desc: String
        get() = "The alarm is $status"

    private val status: String
        get() = when {
            on -> "ringing"
//            rangToday -> "already rang today"
            next.disabled -> "disabled $nextDayName"
            next.overridden && next.time != null -> "overridden to ring $nextDayName at ${next.time}"
            next.overridden -> "overridden to not ring $nextDayName"
            next.auto -> "autoset to ring $nextDayName at ${next.time}"
            next.time == null -> "off $nextDayName"
            else -> "set to ring $nextDayName at ${next.time}"
        }

    private val rangToday: Boolean
        get() = lastTriggered?.today == true

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AlarmData) return false
        if (on != other.on) return false
        if (nextNum != other.nextNum) return false
        if (override != other.override) return false
        if (lastTriggered != other.lastTriggered) return false
        if (days.indices.any { days[it] != other.days[it] }){
            return false
        }

        return today == other.today
    }

    override fun hashCode(): Int {
        var result = on.hashCode()
        result = 31 * result + nextNum
        result = 31 * result + (override?.hashCode() ?: 0)
        result = 31 * result + (lastTriggered?.hashCode() ?: 0)
        result = 31 * result + days.hashCode()
        result = 31 * result + today.hashCode()
        return result
    }

  fun summarize() = status.toInitialCap()

}

@Serializable
data class AlarmOverride(
    @SerialName("days") val days: Int? = 0,
    @SerialName("time") val time: Time?,
    @SerialName("disabled") val disabled: Boolean?
){
//    fun dayIsDisabled(day: Int) = time == null
}

@Serializable
data class Triggered(
    @SerialName("day") val day: String?,
    @SerialName("time") val time: Time?,
    @SerialName("action") val action: String?,
    @SerialName("overridden") val overridden: Boolean,
    @SerialName("disabled") val disabled: Boolean,
    @SerialName("today") val today: Boolean
)

@Serializable
data class Day(
    @SerialName("num") val num: Int,
    @SerialName("index") val index: Int,
    @SerialName("time") val time: Time? = null,
    @SerialName("type") val type: Char,
    @SerialName("original") val original: Time? = null,
    @SerialName("name") val canonicalName: String? = null,
    @SerialName("auto") val auto: Boolean = false,
    @SerialName("overridden") val overridden: Boolean = false,
    @SerialName("disabled") val disabled: Boolean = false
){

    companion object {
        val None: Day = Day(-11111111, -111111, Time.None, 'x')
    }

    @Suppress("unused")
    enum class DayNames {
        Mon, Tue, Wed, Thu, Fri, Sat, Sun
    }

    val week = if (num > 6) 2 else 1

    @Transient
    val name = if (num < 0) "None" else DayNames.entries[num % 7].toString()

    operator fun minus(days: Int) = copy(num = if (num == 0) 13 else num - days)
    operator fun plus(days: Int) = copy(num = if (num == 13) 0 else num + days)
    operator fun compareTo(other: Day) = this.num.compareTo(other.num)
    operator fun rangeTo(other: Day) = (num..other.num).map { copy(num = it) }

    private fun Char.toDayType(): String = when (this){
        'a' -> "A"
        'b', 'm' -> "B"
        else -> ""
    }

    fun desc(inReferenceTo: Day? = null) = when (inReferenceTo?.num){
        this.num -> "today"
        this.num - 1 -> "tomorrow"
        this.num + 1 -> "yesterday"
        else -> name + (type.toDayType().takeIf { it.isNotEmpty() }?.let { " ($it)" } ?: "")
    }

    override fun toString() = "$name/$time"
}

@Serializable(with = TimeSerializer::class)
data class Time(val hour: String, val minute: String) {

    companion object {
        val None = Time("", "")

        fun fromString(str: String?): Time  = when (str){
            null -> None
            "" -> None
            else -> {
                val (hour, min) = str.split(":")
                Time(hour, min)
            }
        }
    }

    override fun toString() = if (this == None) "off" else "${hour.toInt()}:${minute.padStart(2, '0')}"

    fun toMillis(): Long {
        if (this == None) return 0

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour.toInt())
            set(Calendar.MINUTE, minute.toInt())
        }

        if (cal.before(Calendar.getInstance()))
            cal.add(Calendar.DAY_OF_YEAR, 1)

        return cal.timeInMillis
    }
}

fun Time?.toString() = this?.toString() ?: "off"

@ExperimentalSerializationApi
@Serializer(forClass = Time::class)
object TimeSerializer : KSerializer<Time> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Time", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Time = Time.fromString(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Time) {
        encoder.encodeString(value.toString())
    }
}

fun String.toInitialCap(): String = when {
    isEmpty() -> ""
    length == 1 -> get(0).uppercase()
    else -> "${get(0).uppercase()}${substring(1)}"
}