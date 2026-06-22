package com.mozzarelly.homerodeo.data.repo

import com.mozzarelly.homerodeo.data.model.Device
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.util.ApiUnknown
import com.mozzarelly.homerodeo.data.api.DevicesApi
import com.mozzarelly.homerodeo.util.map
import com.mozzarelly.homerodeo.util.resultOrElse
import com.mozzarelly.homerodeo.util.toUiStateFlow
import com.mozzarelly.homerodeo.util.update
import com.mozzarelly.homerodeo.util.updateCopying
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

typealias DeviceFlow = StateFlow<ApiResponse<Device>>

@JvmInline
value class DeviceName(val string: String)

@JvmInline
value class DeviceAlias(val string: String)

@Singleton
class DevicesRepository @Inject constructor(
  private val namesRepository: DeviceNamesRepository,
  private val api: DevicesApi,
) {
  companion object { var count = 0 }

  private val deviceFlows = ConcurrentHashMap<DeviceAlias, DeviceFlow>()
  private lateinit var aliases: List<DeviceAlias>
  private lateinit var aliasToNameMappings: Map<DeviceAlias, DeviceName>
  private lateinit var nameToAliasMappings: Map<DeviceName, DeviceAlias>

  val favoriteDevices: List<DeviceAlias> by lazy { namesRepository.getRecentDevices().mapNotNull { nameToAliasMappings[it] } }

  init {
    loadAliases()
  }

  val allFlows: List<Pair<DeviceAlias, DeviceFlow>> = aliases.map {
    it to getDeviceFlowByAlias(it)
  }

  fun recentDeviceFlows(scope: CoroutineScope) = namesRepository.getRecentDevices(5).map {
    nameToAliasMappings[it]!! to getDeviceFlowByName(it).toUiStateFlow(scope)
  }

  fun getDeviceFlowByAlias(alias: DeviceAlias): DeviceFlow = deviceFlows.getOrPut(alias) {
    MutableStateFlow<ApiResponse<Device>>(ApiUnknown())
  }

  fun getDeviceFlowByName(name: DeviceName): DeviceFlow = deviceFlows.getOrPut(nameToAliasMappings[name]) {
    MutableStateFlow<ApiResponse<Device>>(ApiUnknown())
  }

  suspend fun deviceOn(alias: DeviceAlias, lock: Boolean, timer: Int?) {
    val name = getName(alias)
    updateDevice(alias) {
      if (lock)
        api.postDevice(name.string, "on", "lock")
      else if (timer != null)
        api.postDevice(name.string, "on", timer.toString())
      else
        api.deviceOn(name.string)
    }
  }

  suspend fun deviceOff(alias: DeviceAlias, lock: Boolean, timer: Int?) {
    val name = getName(alias)
    updateDevice(alias) {
      if (lock)
        api.postDevice(name.string, "off", "lock")
      else if (timer != null)
        api.postDevice(name.string, "off", timer.toString())
      else
        api.deviceOff(name.string)
    }
  }

  suspend fun deviceOffByName(name: DeviceName, lock: Boolean, timer: Int?) {
    val alias = getAlias(name)
    updateDevice(alias) {
      if (lock)
        api.postDevice(name.string, "off", "lock")
      else if (timer != null)
        api.postDevice(name.string, "off", timer.toString())
      else
        api.deviceOff(name.string)
    }
  }

  suspend fun toggleDevice(alias: DeviceAlias) {
    val name = getName(alias)
    updateDevice(alias) {
      api.deviceToggle(name.string)
    }
  }

  suspend fun setFavoriteStatus(alias: DeviceAlias, favorite: Boolean){
    val name = getName(alias)
    namesRepository.toggleFavoriteStatus(name)
  }

  suspend fun toggleDeviceByName(name: DeviceName) {
    val alias = getAlias(name)
    updateDevice(alias) {
      api.deviceToggle(name.string)
    }
  }

  private suspend fun loadDevice(alias: DeviceAlias, source: String){
    val name = getName(alias)
    val flow = getDeviceFlowByAlias(alias)
    (flow as MutableStateFlow).update {
      api.getDevice(name.string)
        .also { println("zzzdev refresh for $source: $name/$alias to $it") }
    }
  }

  suspend fun toggleCustom(forWhom: String) {
    api.toggleCustom(forWhom)
  }

  fun invalidateAliases(){
    namesRepository.setDeviceGroups(null)
  }

  fun clearRecentDevices(){
    namesRepository.clearRecentDevices()
  }

  private fun loadAliases(){
    runBlocking {
      if (!namesRepository.hasData){
        val groups = api.getDeviceGroups().resultOrElse { error("Loading device groups failed.") }
        namesRepository.setDeviceGroups(groups)
      }

      aliases = namesRepository.getDeviceAliases()
      nameToAliasMappings = namesRepository.getNameToAliasMappings()
      aliasToNameMappings = namesRepository.getAliasToNameMappings()
    }
  }

  private suspend fun updateDevice(alias: DeviceAlias, block: suspend () -> ApiResponse<Device>) {
    val flow = getDeviceFlowByAlias(alias)
    flow.updateCopying { it.map { it.copy(incomplete = true) } }
    val device = block()
    flow.update { device }
//    device.dataOrNull()?.name?.let { dao.saveRecentDevice(it) }
  }

  private fun getName(alias: DeviceAlias): DeviceName {
    return aliasToNameMappings[alias] ?: error("No name for alias $alias")
  }

  private fun getAlias(name: DeviceName): DeviceAlias{
    return nameToAliasMappings[name] ?: error("No alias for name $name")
  }

  suspend fun refreshByName(name: DeviceName, source: String) {
    val alias = namesRepository.getNameToAliasMappings()[name] ?: error("Can't get alias for $name")
    refresh(alias, source)
  }

  suspend fun refresh(device: DeviceAlias, source: String) {
    loadDevice(device, source)
  }

}