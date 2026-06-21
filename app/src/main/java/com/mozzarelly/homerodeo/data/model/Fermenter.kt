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
import kotlin.math.roundToInt

@Serializable
data class Fermenter(
  @SerialName("mode") val mode: FermenterMode,
  @SerialName("beerTemp") val productTemp: Temperature,
  @SerialName("beerSetting") val productSetting: Temperature,
  @SerialName("fridgeTemp") val chamberTemp: Temperature,
  @SerialName("fridgeSetting") val chamberSetting: Temperature,
  @SerialName("heater") val heater: Boolean,
  @SerialName("program") val program: FermenterProgram? = null,
  @SerialName("state") val state: String,
  @Transient val chamberTrend: Int = 0,
  @Transient val productTrend: Int = 0,
){
  fun isOn() = mode != FermenterMode.Off
  fun describe(): String = mode.describeWith(this) + if (mode != FermenterMode.Off) "\n${state}" else ""
  fun summarize() = describe()

  companion object {
    val Off = Fermenter(FermenterMode.Off, Temperature.Off, Temperature.Off, Temperature.Off, Temperature.Off, false, null, "Off")
  }
}

@Serializable
data class FermenterProgram(
  @SerialName("name") val name: String,
  @SerialName("steps") val steps: List<String>,
  @SerialName("index") val index: Int,
  @SerialName("currentStep") val currentStep: String?,
  @SerialName("timer") val timer: Int? = null,
) {
  fun currentStepDescription(beerTemp: Temperature? = null, fridgeTemp: Temperature? = null): String? = currentStep?.let { describeStep(it, beerTemp, fridgeTemp) }
  fun stepDescriptions(): List<String> = steps.map { describeStep(it) ?: it }

  private fun describeStep(step: String, beerTemp: Temperature? = null, fridgeTemp: Temperature? = null) : String? {
    fun describeTemp(temp: Temperature?) = temp?.let { " (@${it.rounded()})" } ?: ""

    if (step == "notify") return "Wait for user action"
    if (step == "alert") return "Alert user"

    step[0].takeIf { it == '+' || it == '-' }?.let {
      return "Turn ${step.substring(1)} ${if (it == '-') "off" else "on"}"
    }

    val (_, type, op, value) = "([bwf])([<>=])=?([0-9]+)".toRegex().matchEntire(step)?.groupValues ?: return null
    return when (type to op){
      "b" to "=" -> "Hold product at $value" + describeTemp(beerTemp)
      "b" to ">" -> "Bring product up to $value" + describeTemp(beerTemp)
      "b" to "<" -> "Bring product down to $value" + describeTemp(beerTemp)
      "f" to "=" -> "Hold chamber at $value" + describeTemp(fridgeTemp)
      "f" to ">" -> "Bring chamber up to $value" + describeTemp(fridgeTemp)
      "f" to "<" -> "Bring chamber down to $value" + describeTemp(fridgeTemp)
      "w" to "=" -> "Wait for $value minutes" + (timer?.let { " ($it left)" } ?: "")
      else -> error("Can't handle type $type and op $op")
    }
  }
}

@Serializable
enum class FermenterMode(private val desc: String) {
  @SerialName("Beer constant") Product("Product"),
  @SerialName("Fridge constant") Chamber("Chamber"),
  @SerialName("Off") Off("Off");

  fun describeWith(ferm: Fermenter): String = when (this) {
    Off -> desc
    Product -> "Holding $desc at ${ferm.productSetting} (${ferm.productTemp})"
    Chamber -> "Holding $desc at ${ferm.chamberSetting} (${ferm.chamberTemp})"
  }
}

@Serializable(with = TemperatureSerializer::class)
data class Temperature(
  @SerialName("temp") val tempString: String
){
  constructor(temp: Float): this(temp.toFormattedString())

  val isSet: Boolean
    get() = temp != null

  val temp: Float?
    get() = if (this == Off) null else tempString.toFloatOrNull()

  override fun toString(): String = tempString.replace(Regex("\\.$"), "").run {
    when (length) {
        5 -> "${replace(Regex("\\..$"), "")}°"
      else -> "$this°"
    }
  }

  fun rounded(): String = toString().replace(Regex("[^0-9.-]"), "").toFloat().roundToInt().toString() + "°"

  operator fun compareTo(other: Temperature) = if (temp == null) {
    if (other.temp == null) 0 else -1
  }
  else {
    temp!!.compareTo(other.temp!!)
  }

  companion object {
    val Off = Temperature("-.--")
  }
}

private fun Float.toFormattedString(): String = if (this >= 100)
  roundToInt().toString()
else
  ((this * 10).roundToInt() / 10.0).toString()


@ExperimentalSerializationApi
@Serializer(forClass = Temperature::class)
object TemperatureSerializer : KSerializer<Temperature> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Temperature", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): Temperature = Temperature(decoder.decodeString())

  override fun serialize(encoder: Encoder, value: Temperature) {
    encoder.encodeString(value.tempString)
  }
}