package com.mozzarelly.homerodeo.data.model

import androidx.recyclerview.widget.DiffUtil
import com.mozzarelly.homerodeo.data.repo.DeviceAlias
import com.mozzarelly.homerodeo.data.repo.DeviceName
import com.mozzarelly.rodeo.devices.drawableForLocation
import com.mozzarelly.rodeo.devices.drawableForType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface RodeoControl {
    val name: DeviceName
    val alias: DeviceAlias
    val toggledOn: Boolean
    val reachable: Boolean
    val description: String
    val stateText: String
    val stateSubtext: String
}

@Serializable
data class Device(
    @SerialName("name") val _name: String,
    @SerialName("alias") val _alias: String,
    @SerialName("on") val on: Boolean? = null,
    @SerialName("offline") val offline: Boolean = false,
    @SerialName("overridden") val overridden: Boolean = false,
    @SerialName("incomplete") val incomplete: Boolean = false,
    @SerialName("schedule") val schedule: Schedule? = null,
    @SerialName("timer") val timerSpec: String? = null,
    @SerialName("locksOnToggle") val locksOnToggle: Boolean = false,
    var loaded: Boolean = false
): RodeoControl {
    constructor(name: String, alias: String) : this(name, alias, false, false)
    constructor(name: String, alias: String, offline: Boolean) : this(name, alias, false, offline)

    override val alias: DeviceAlias
        get() = DeviceAlias(_alias)
    
    override val name: DeviceName
        get() = DeviceName(_name)
    
    val timer: Pair<Boolean, String>?
        get() = timerSpec?.split("=")?.let {
            (it[0] == "on") to it[1]
        }

    val priority: Int = when (name){
        DeviceName("office") -> 4
        DeviceName("garagedoor") -> 5
        else -> 1
    }

    override val toggledOn: Boolean
        get() = isOn()

    override val reachable: Boolean
        get() = !offline

    override val description: String
        get() = alias.string

    val iconForType get() = drawableForType(alias.string)
    val iconForLocation get() = drawableForLocation(alias.string)

//    var TODO: ability to set lock and power independently

    override val stateText: String
        get() = when {
            toggledOn && name.string == "garagedoor" -> "Open"
            toggledOn -> "On"
            name.string == "garagedoor" -> "Closed"
            else -> "Off"
        }

    override val stateSubtext = ""

    fun desc() = alias.string.capitalize()
    fun isOn() = on ?: false
    fun isOverridden() = overridden
    fun isOffline(): Boolean = offline

    override fun toString(): String = """${alias ?: name} (${on})"""

    companion object {
        val Differ = object: DiffUtil.ItemCallback<Device>() {
            override fun areItemsTheSame(p0: Device, p1: Device): Boolean = p0.name == p1.name
            override fun areContentsTheSame(p0: Device, p1: Device): Boolean = p0 == p1
        }
    }
}

@Serializable
data class Schedule(
    @SerialName("on") val on: String? = null,
    @SerialName("off") val off: String? = null,
    @SerialName("history") val history: String? = null
)