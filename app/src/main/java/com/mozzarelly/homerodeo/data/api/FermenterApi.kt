package com.mozzarelly.homerodeo.data.api

import com.mozzarelly.homerodeo.data.model.Fermenter
import com.mozzarelly.homerodeo.util.ApiResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FermenterApi {
    @GET("beer")
    suspend fun getFermenter(@Query("source") source: String): ApiResponse<Fermenter>

    @POST("beer/{setting}/{temp}")
    suspend fun setMode(@Path("setting") setting: String, @Path("temp") temp: String): ApiResponse<Fermenter>

    @PUT("beer/heater")
    suspend fun setHeater(): ApiResponse<Fermenter>

    @DELETE("beer/heater")
    suspend fun unsetHeater(): ApiResponse<Fermenter>

    @POST("beerprogram/{program}")
    suspend fun runProgram(@Path("program") program: String): ApiResponse<Unit>

    @PUT("beerprogram")
    suspend fun advanceProgram(): ApiResponse<Unit>

    @DELETE("beerprogram")
    suspend fun stopProgram(): ApiResponse<Unit>
}