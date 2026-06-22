package com.mozzarelly.homerodeo.data.api

import com.mozzarelly.homerodeo.data.model.Device
import com.mozzarelly.homerodeo.data.model.DeviceGroups
import com.mozzarelly.homerodeo.data.model.Devices
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.data.repo.DeviceName
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


interface DevicesApi {
    @GET("devicegroups")
    suspend fun getDeviceGroups(): ApiResponse<DeviceGroups>

    @GET("devicedefs")
    suspend fun getDeviceDefinitions(): ApiResponse<Devices>

//    @GET("devicedefs")
//    fun getDeviceDefinitionsFlow(): Flow<ApiResponse<Devices>>

    @GET("device/{name}")
    suspend fun getDevice(@Path("name") name: String): ApiResponse<Device>

//    @GET("device/{name}")
//    fun getDeviceFlow(@Path("name") name: String): Flow<Device>

    @PUT("device/{name}")
    suspend fun deviceOn(@Path("name") name: String): ApiResponse<Device>

    @DELETE("device/{name}")
    suspend fun deviceOff(@Path("name") name: String): ApiResponse<Device>

    @POST("device/{name}")
    suspend fun deviceToggle(@Path("name") name: String): ApiResponse<Device>

    @POST("device/{name}/{state}/{override}")
    suspend fun postDevice(@Path("name") name: String, @Path("state") state: String, @Path("override") override: String): ApiResponse<Device>

    @POST("device/custom/{forWhom}")
    suspend fun toggleCustom(@Path("forWhom") forWhom: String): ApiResponse<Device>

    @POST("action/{action}")
    suspend fun doAction(@Path("action") action: String)

    suspend fun setFavoriteStatus(name: DeviceName, favorite: Boolean)
}
