package com.mozzarelly.homerodeo.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Devices(
  @SerialName("devices") val devices: List<Device>,
)