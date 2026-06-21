package com.mozzarelly.homerodeo.data.api

import com.mozzarelly.homerodeo.data.model.Fermenter
import com.mozzarelly.homerodeo.data.model.FermenterMode
import com.mozzarelly.homerodeo.data.model.FermenterProgram
import com.mozzarelly.homerodeo.data.model.Temperature
import com.mozzarelly.homerodeo.data.ApiResponse
import com.mozzarelly.homerodeo.data.ApiResult
import com.mozzarelly.homerodeo.data.map
import kotlinx.coroutines.delay

class FakeFermenterApi : FermenterApi {
    var fermenter: ApiResponse<Fermenter> = ApiResult(Fermenter(
        mode = FermenterMode.Chamber,
        productTemp = Temperature(64.0f),
        productSetting = Temperature(65.2f),
        chamberTemp = Temperature(64.0f),
        chamberSetting = Temperature(64.0f),
        heater = true,
        program = null,
        state = "Idle"
    ))

    override suspend fun getFermenter(source: String): ApiResponse<Fermenter> {
        delay(2000)
        return fermenter
    }

    override suspend fun setMode(setting: String, temp: String): ApiResponse<Fermenter> {
        delay(2000)
        fermenter = when (setting) {
            "fridge" -> fermenter.map { it.copy(chamberSetting = Temperature(temp), mode = FermenterMode.Chamber) }
            "beer" -> fermenter.map { it.copy(productSetting = Temperature(temp), mode = FermenterMode.Product) }
            else -> fermenter.map {
                it.copy(
                    mode = FermenterMode.Off,
                    productSetting = Temperature.Off,
                    chamberSetting = Temperature.Off
                )
            }
        }

        return fermenter
    }

    override suspend fun setHeater(): ApiResponse<Fermenter> {
        delay(2000)
        fermenter = fermenter.map { it.copy(heater = true) }
        return fermenter
    }

    override suspend fun unsetHeater(): ApiResponse<Fermenter> {
        delay(2000)
        fermenter = fermenter.map { it.copy(heater = false) }
        return fermenter
    }

    override suspend fun runProgram(programName: String): ApiResponse<Unit> {
        delay(2000)
        fermenter = fermenter.map { it.copy(program = FermenterProgram(programName, listOf("1", "2"), 0, "1")) }
        return fermenter.map { }
    }

    override suspend fun advanceProgram(): ApiResponse<Unit> {
        delay(2000)
        TODO("Not yet implemented")
    }

    override suspend fun stopProgram(): ApiResponse<Unit> {
        delay(2000)
        fermenter = fermenter.map { it.copy(program = null) }
        return fermenter.map { }
    }

}