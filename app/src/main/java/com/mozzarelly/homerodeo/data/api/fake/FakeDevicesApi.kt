package com.mozzarelly.homerodeo.data.api.fake

import com.mozzarelly.homerodeo.data.api.DevicesApi
import com.mozzarelly.homerodeo.data.model.Device
import com.mozzarelly.homerodeo.data.model.DeviceGroup
import com.mozzarelly.homerodeo.data.model.DeviceGroups
import com.mozzarelly.homerodeo.data.model.Devices
import com.mozzarelly.homerodeo.data.repo.DeviceName
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.util.ApiResult
import kotlinx.coroutines.delay

class FakeDevicesApi : DevicesApi {

    private val deviceList = mutableListOf(
        Device("office",      "Office",      on = true),
        Device("outside",     "Outside",     on = false),
        Device("fishfilter",  "Fish Filter", on = true),
        Device("terrarium",   "Terrarium",   on = true),
        Device("porch",       "Porch",       on = false),
        Device("garagedoor",  "Garage Door", on = false),
        Device("couchlamp",   "Couch Lamp",  on = false),
        Device("aquarium",    "Aquarium",    on = true),
    )

    private val groups = DeviceGroups(
        groups = listOf(
            DeviceGroup("Living Room", devices = listOf("couchlamp", "aquarium")),
            DeviceGroup("Outside",     devices = listOf("outside", "porch", "garagedoor")),
            DeviceGroup("Office",      devices = listOf("office")),
            DeviceGroup("Other",       devices = listOf("fishfilter", "terrarium")),
        ),
        aliases = deviceList.associate { it._name to it._alias },
    )

    private fun findDevice(name: String): Device =
        deviceList.firstOrNull { it._name == name }
            ?: Device(name, name, offline = true)

    private fun updateDevice(name: String, update: (Device) -> Device): ApiResponse<Device> {
        val idx = deviceList.indexOfFirst { it._name == name }
        val updated = update(findDevice(name))
        if (idx >= 0) deviceList[idx] = updated
        return ApiResult(updated)
    }

    override suspend fun getDeviceGroups(): ApiResponse<DeviceGroups> {
        delay(500)
        return ApiResult(groups)
    }

    override suspend fun getDeviceDefinitions(): ApiResponse<Devices> {
        delay(500)
        return ApiResult(Devices(deviceList.toList()))
    }

    override suspend fun getDevice(name: String): ApiResponse<Device> {
        delay(300)
        return ApiResult(findDevice(name))
    }

    override suspend fun deviceOn(name: String): ApiResponse<Device> {
        delay(500)
        return updateDevice(name) { it.copy(on = true) }
    }

    override suspend fun deviceOff(name: String): ApiResponse<Device> {
        delay(500)
        return updateDevice(name) { it.copy(on = false) }
    }

    override suspend fun deviceToggle(name: String): ApiResponse<Device> {
        delay(500)
        return updateDevice(name) { it.copy(on = !(it.on ?: false)) }
    }

    override suspend fun postDevice(name: String, state: String, override: String): ApiResponse<Device> {
        delay(500)
        return updateDevice(name) { it.copy(on = state == "on", overridden = override == "lock") }
    }

    override suspend fun toggleCustom(forWhom: String): ApiResponse<Device> {
        delay(500)
        return ApiResult(findDevice(forWhom))
    }

    override suspend fun doAction(action: String) {
        delay(500)
    }

    override suspend fun setFavoriteStatus(name: DeviceName, favorite: Boolean) {
        // stored client-side only; no-op in fake
    }
}
