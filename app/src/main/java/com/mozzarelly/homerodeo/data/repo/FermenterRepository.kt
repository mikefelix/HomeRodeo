package com.mozzarelly.homerodeo.data.repo

import com.mozzarelly.homerodeo.data.model.Fermenter
import com.mozzarelly.homerodeo.data.model.Temperature
import com.mozzarelly.homerodeo.data.ApiResponse
import com.mozzarelly.homerodeo.data.ApiUnknown
import com.mozzarelly.homerodeo.data.api.FermenterApi
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FermenterRepository @Inject constructor(
  private val api: FermenterApi
)  {
  companion object { var count = 0 }

  val fermenterFlow = MutableStateFlow<ApiResponse<Fermenter>>(ApiUnknown())

  suspend fun getFermenter(source: String) = api.getFermenter(source)

  suspend fun refresh(source: String){
    println("zzzferm refresh from $source")
    val fermenter = getFermenter(source)
    println("zzzferm refreshed from $source: ${fermenter.isSuccess}")
    fermenterFlow.value = fermenter
  }

  suspend fun setMode(setting: String, temp: Temperature): ApiResponse<Fermenter> {
    return api.setMode(setting, temp.tempString)
      .also { refresh("setMode") }
  }

  suspend fun setHeater(): ApiResponse<Fermenter> {
    return api.setHeater()
      .also { refresh("setHeater") }
  }

  suspend fun unsetHeater(): ApiResponse<Fermenter> {
    return api.unsetHeater()
      .also { refresh("unsetHeater") }
  }

  suspend fun runProgram(program: String) {
    api.runProgram(program)
      .also { refresh("runProgram") }
  }

  suspend fun advanceProgram() {
    api.advanceProgram()
      .also { refresh("advanceProgram") }
  }

  suspend fun stopProgram(){
    api.stopProgram()
      .also { refresh("stopProgram") }
  }

  suspend fun startYogurtProgram(): ApiResponse<Unit> = api.runProgram("yogurt")
    .also { refresh("yogurt") }

}