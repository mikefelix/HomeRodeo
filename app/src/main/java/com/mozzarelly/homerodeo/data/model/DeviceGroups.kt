package com.mozzarelly.homerodeo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceGroups(
  @SerialName("groups") val groups: List<DeviceGroup>,
  @SerialName("aliases") val aliases: Map<String, String>,
  @SerialName("ranges") val ranges: List<Range>? = null,
  @SerialName("thermostat") val thermostat: Thermostat? = null
)