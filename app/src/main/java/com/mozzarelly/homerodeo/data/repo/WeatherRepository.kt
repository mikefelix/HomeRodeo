package com.mozzarelly.homerodeo.data.repo

import com.mozzarelly.homerodeo.data.api.WeatherApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val api: WeatherApi
) {
    companion object { var count = 0 }

    suspend fun getWeather() = api.getWeather()

    val weatherFlow = flow {
        while (true) {
            emit(api.getWeather())
            delay(60000)
        }
    }
}