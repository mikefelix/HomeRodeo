package com.mozzarelly.homerodeo.data.repo

import android.content.SharedPreferences
import androidx.core.content.edit
import com.mozzarelly.homerodeo.data.model.DeviceGroups
import com.mozzarelly.homerodeo.util.get
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

const val DeviceGroupsKey = "deviceGroups"
const val RecentDevicesKey = "recentDevices"

class DeviceNamesRepository @Inject constructor(
  private val sharedPrefs: SharedPreferences
){
  private val defaultRecentDevices = "office,outside,fishfilter,terrarium,porch"

  val hasData: Boolean = false //sharedPrefs.getString(DeviceGroupsKey, null) != null

  fun getRecentDevices(num: Int = Int.MAX_VALUE): List<DeviceName> {
    val names = getDeviceNames()
//    return sharedPrefs.getString(RecentDevicesKey, defaultRecentDevices)
    return defaultRecentDevices
      .split(",")
      .map { DeviceName(it) }
      .filter { it in names }
      .take(num)
  }

  fun clearRecentDevices(){
    sharedPrefs.edit {
      remove(RecentDevicesKey)
    }
  }

  fun toggleFavoriteStatus(name: DeviceName){
    val current = getRecentDevices()
    val new = if (name !in current) {
      current - setOf(name)
    }
    else {
      listOf(name) + current
    }

    sharedPrefs.edit {
      putString(RecentDevicesKey, new.joinToString(","){ it.string })
    }
  }

  fun setDeviceGroups(groups: DeviceGroups?){
    sharedPrefs.edit {
      if (groups == null)
        remove(DeviceGroupsKey)
      else
        putString(DeviceGroupsKey, Json.encodeToString(groups))
    }
  }

  fun getAliasToNameMappings(): Map<DeviceAlias, DeviceName> {
    return getNameToAliasMappings().invert()
  }

  fun getNameToAliasMappings(): Map<DeviceName, DeviceAlias> {
    val (groups, aliases) = getDeviceGroups() ?: return emptyMap()
    val names: List<DeviceName> = groups.flatMap { it.devices }.map { DeviceName(it)}

    return names.associateWith {
      DeviceAlias(aliases.getOrElse(it.string) { it.string })
    }
  }

  fun getDeviceAliases(): List<DeviceAlias> {
    val names = getDeviceNames()
    return getDeviceGroups()
      ?.aliases
      ?.filter { DeviceName(it.key) in names }
      ?.values
      ?.sorted()
      ?.map { DeviceAlias(it) }
      ?: error("Can't get aliases.")
  }

  private fun getDeviceNames(): List<DeviceName> = getDeviceGroups()?.groups
    ?.flatMap { it.devices }
    ?.sorted()
    ?.map { DeviceName(it) }
    ?: emptyList()

  private fun getDeviceGroups(): DeviceGroups? = sharedPrefs.get<String>(DeviceGroupsKey)?.let {
    Json.decodeFromString<DeviceGroups>(it)
  }

  private fun <A, B> Map<A, B>.invert(): Map<B, A> = keys.associateBy { get(it)!! }
}
