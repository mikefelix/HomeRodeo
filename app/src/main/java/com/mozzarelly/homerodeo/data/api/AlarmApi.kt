package com.mozzarelly.homerodeo.data.api

import com.mozzarelly.homerodeo.data.model.Alarm
import com.mozzarelly.homerodeo.data.ApiResponse
import retrofit2.http.*

/*
{
    "on":false,
    "next":{"day":"tomorrow","enabled":true,"setting":"07:10"},
    "setting":"07:10","hasTriggeredToday":true,
    "ringTimeToday":"07:10",
    "lastTriggered":{"day":"20190418","setting":"07:10","action":"rung"},
    "times":["09:00","07:30","07:10","07:10","07:10","07:10","10:00"],
    "enabled":[false,true,true,true,true,true,true]
}
 */
interface AlarmApi {
    @GET("alarm")
    suspend fun getAlarm(): ApiResponse<Alarm>

    @PUT("alarm")
    suspend fun setAlarm(@Body alarm: Alarm): Alarm

    @POST("alarm/{day}/{set}")
    suspend fun saveSetting(@Path("day") day: String, @Path("set") set: String)

    @POST("alarm/o{day}/{set}")
    suspend fun saveOverride(@Path("day") day: String, @Path("set") set: String)

    @POST("alarm/t1/{set}")
    suspend fun saveOverrideRange(@Path("set") set: String)

    @POST("alarm/t{days}/off")
    suspend fun disable(@Path("days") days: Int)

    @POST("alarm/t{days}/on")
    suspend fun undisable(@Path("days") days: String)

}