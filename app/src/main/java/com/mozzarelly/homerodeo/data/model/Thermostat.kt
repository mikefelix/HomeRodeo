package com.mozzarelly.homerodeo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*
"thermostat": {
    "away": false,
    "temp": 68,
    "target": 68,
    "humidity": 35,
    "state": "off",
    "on": false,
    "mode": "heat"
  }

 */
@Serializable
data class Thermostat(
    @SerialName("away") val away: Boolean,
    @SerialName("on") val on: Boolean,
    @SerialName("temp") val temp: Int,
    @SerialName("target") val target: Int,
    @SerialName("humidity") val humidity: Int,
    val mode: String
){
    constructor(): this(false, false, 60, 60, 60, "eco")
}