package com.mozzarelly.homerodeo.data.api

import com.mozzarelly.homerodeo.data.model.Temperature
import com.mozzarelly.homerodeo.data.ApiResponse
import com.mozzarelly.homerodeo.data.model.Inside
import com.mozzarelly.homerodeo.data.model.Outside
import com.mozzarelly.homerodeo.data.model.SecurityMode
import com.mozzarelly.homerodeo.data.model.Weather
import retrofit2.http.*

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(): ApiResponse<Weather>

    @GET("inside")
    suspend fun getInside(): Inside

    @GET("outside")
    suspend fun getOutside(): Outside

    @PUT("therm/away")
    suspend fun setAway(): Inside

    @DELETE("therm/away")
    suspend fun setHome(): Inside

    @POST("therm/temp{temp}")
    suspend fun setTemp(@Path("temp") temp: Temperature): Inside

    @POST("therm/fan{time}")
    suspend fun runFan(@Path("time") time: Int): Inside

    @POST("security/{state}")
    suspend fun setSecurity(@Path("state") state: SecurityMode): Inside

    @POST("action/cominghome")
    suspend fun comingHome()

    @POST("action/leavinghome")
    suspend fun leavingHome()

    @POST("action/sleeping")
    suspend fun sleeping()
}