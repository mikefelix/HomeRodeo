package com.mozzarelly.homerodeo.data.api.fake

import com.mozzarelly.homerodeo.data.api.WeatherApi
import com.mozzarelly.homerodeo.data.model.AirQuality
import com.mozzarelly.homerodeo.data.model.FurnaceMode
import com.mozzarelly.homerodeo.data.model.FurnaceState
import com.mozzarelly.homerodeo.data.model.Inside
import com.mozzarelly.homerodeo.data.model.Outside
import com.mozzarelly.homerodeo.data.model.SecurityMode
import com.mozzarelly.homerodeo.data.model.Temperature
import com.mozzarelly.homerodeo.data.model.Weather
import com.mozzarelly.homerodeo.util.ApiResponse
import com.mozzarelly.homerodeo.util.ApiResult
import kotlinx.coroutines.delay

class FakeWeatherApi : WeatherApi {

    private var inside = Inside(
        away = false,
        temp = Temperature(71.0f),
        target = Temperature(70.0f),
        humidity = 42f,
        state = FurnaceState.Off,
        on = true,
        mode = FurnaceMode.Heat,
        security = SecurityMode.Disarmed,
        occupancy = "home",
        since = null,
    )

    private val outside = Outside(
        temp = Temperature(58.0f),
        feelsLike = Temperature(55.0f),
        humidity = 65f,
        low = Temperature(48.0f),
        high = Temperature(63.0f),
        cond = "partly cloudy",
        windspeed = 8.5f,
        rain = null,
        snow = null,
        clouds = 40f,
        airQuality = AirQuality(pm25 = "5.2", ozone = "28", updated = "2:00 PM", desc = "good"),
        night = false,
        forecast = "partly cloudy skies",
    )

    override suspend fun getWeather(): ApiResponse<Weather> {
        delay(500)
        return ApiResult(Weather(inside = inside, outside = outside))
    }

    override suspend fun getInside(): Inside {
        delay(300)
        return inside
    }

    override suspend fun getOutside(): Outside {
        delay(300)
        return outside
    }

    override suspend fun setAway(): Inside {
        delay(500)
        inside = inside.copy(away = true, mode = FurnaceMode.Eco)
        return inside
    }

    override suspend fun setHome(): Inside {
        delay(500)
        inside = inside.copy(away = false, mode = FurnaceMode.Heat)
        return inside
    }

    override suspend fun setTemp(temp: Temperature): Inside {
        delay(500)
        inside = inside.copy(target = temp)
        return inside
    }

    override suspend fun runFan(time: Int): Inside {
        delay(500)
        inside = inside.copy(state = FurnaceState.Fan)
        return inside
    }

    override suspend fun setSecurity(state: SecurityMode): Inside {
        delay(500)
        inside = inside.copy(security = state)
        return inside
    }

    override suspend fun comingHome() { delay(500) }
    override suspend fun leavingHome() { delay(500) }
    override suspend fun sleeping() { delay(500) }
}
