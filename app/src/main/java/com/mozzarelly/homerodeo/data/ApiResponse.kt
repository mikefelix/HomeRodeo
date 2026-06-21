package com.mozzarelly.homerodeo.data

import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicInteger

/**
 * One purpose of this utility is to smooth out a few issues with Retrofit.
 * If the API can return 204, you can get a crash if you don't use Response<T> as your return type.
 * But if you use Response<T>, exceptions won't be thrown from 400- or 500-level response codes.
 * Also, Retrofit is in Java so the semantics of a nullable wrapped type in Response aren't explicit.
 * Furthermore, using Response directly means always having to check isSuccessful before calling body(),
 * and forgetting to do so could cause an exception.
 *
 * Here we encapsulate the response in a wrapper that ensures:
 * 1. Nullability is handled. If I declare Response<String> and get an empty body, I'll get ApiEmpty
 *    instead of an ApiResponse containing data.
 * 2. Errors must be explicitly handled rather than relying on exceptions. This is because we have
 *    too many hidden gotchas in the app where a disappearing network can result in an exception
 *    bubbling out and crashing the app.
 * 3. A successful response object can't be obtained without also dealing with the error condition
 *    that would arise if the network was gone.
 */
sealed interface ApiResponse<T: Any> {
  val isSuccess: Boolean
    get() = this is ApiSuccess<T>

  fun dataOrNull(): T? = if (this is ApiResult) data else null
  fun debug(describeData: (T) -> String): String = when (this){
    is ApiResult -> "ApiResult(${describeData(data)})"
    is ApiEmpty -> "ApiEmpty"
    is ApiUnknown -> "ApiUnknown"
    is ApiError -> "ApiError($code, $message)"
    is ApiException -> "ApiException(${e::class.java}: ${e.message})"
  }
}

inline fun <T: Any, U: Any> ApiResponse<T>.map(func: (T) -> U): ApiResponse<U> = when (this){
  is ApiResult -> ApiResult(func(data))
  is ApiEmpty -> ApiEmpty()
  is ApiUnknown -> ApiUnknown()
  is ApiError -> ApiError(code, message)
  is ApiException -> ApiException(e)
}

sealed class ApiSuccess<T: Any> : ApiResponse<T>
sealed class ApiFailure<T: Any> : ApiResponse<T>

class ApiResult<T: Any>(val data: T) : ApiSuccess<T>(){
  override fun toString(): String = "ApiResult($data)"
}
class ApiUnknown<T: Any> : ApiSuccess<T>()
class ApiEmpty<T: Any> : ApiSuccess<T>()
class ApiError<T: Any>(val code: Int, val message: String?) : ApiFailure<T>()
class ApiException<T: Any>(val e: Throwable) : ApiFailure<T>()

class ApiResponseCall<T: Any>(private val wrapped: Call<T>, private val testingConfig: NetworkTestingConfig) : Call<ApiResponse<T>> {
  override fun enqueue(callback: Callback<ApiResponse<T>>) {
    wrapped.enqueue(object : Callback<T> {
      private val retryAttempts = AtomicInteger(0)

      override fun onResponse(call: Call<T>, response: Response<T>) {
        if (testingConfig.shouldFailThisCall(call.request())){
          println("zzzApiResponse failing a call")
          callback.onFailure(this@ApiResponseCall, IOException("Testing call failure."))
        }

        val apiResponse: ApiResponse<T> = if (response.isSuccessful) {
          val body = response.body()
          if (body == null)
            ApiEmpty()
          else
            ApiResult(body)
        }
        else {
          ApiError(code = response.code(), message = response.message())
        }
        callback.onResponse(this@ApiResponseCall, Response.success(apiResponse))
      }

      override fun onFailure(call: Call<T>, t: Throwable) {
        if (retryAttempts.incrementAndGet() > 8) {
          println("zzzApiResponse Giving up on call after too many failures.")
          callback.onResponse(this@ApiResponseCall, Response.success(ApiException(t)))
        }
        else {
          println("zzzApiResponse retrying #${retryAttempts.get()}.")
          call.clone().enqueue(this)
        }
      }
    })
  }

  override fun execute(): Response<ApiResponse<T>> = throw NotImplementedError()
  override fun clone(): Call<ApiResponse<T>> = ApiResponseCall(wrapped.clone(), testingConfig)
  override fun request(): Request = wrapped.request()
  override fun timeout(): Timeout = wrapped.timeout()
  override fun isExecuted(): Boolean = wrapped.isExecuted
  override fun isCanceled(): Boolean = wrapped.isCanceled
  override fun cancel() { wrapped.cancel() }
}


class ApiResponseCallAdapter(private val resultType: Type, private val testingConfig: NetworkTestingConfig) : CallAdapter<Type, Call<ApiResponse<Type>>> {
  override fun responseType(): Type = resultType
  override fun adapt(call: Call<Type>): Call<ApiResponse<Type>> = //if (testingConfig.failCallsPercent > 0f)
    ApiResponseCall(call, testingConfig)
//  else
//    TestingApiResponseCall(call, testingConfig::shouldFailThisCall)
}

class ApiResponseCallAdapterFactory(private val testingConfig: NetworkTestingConfig) : CallAdapter.Factory() {
  override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
    if (getRawType(returnType) != Call::class.java) {
      return null
    }

    val callType = getParameterUpperBound(0, returnType as ParameterizedType)
    if (getRawType(callType) != ApiResponse::class.java) {
      return null
    }

    val resultType = getParameterUpperBound(0, callType as ParameterizedType)
    return ApiResponseCallAdapter(resultType, testingConfig)
  }
}

interface NetworkTestingConfig {
  val failCallsPercent: Float
  fun shouldFailThisCall(request: Request): Boolean
}

inline fun <reified T: Any> ApiResponse<T>.resultOrElse(orElse: () -> T): T = if (this is ApiResult<T>) data else orElse()
inline fun <reified T: Any> ApiResponse<T>.resultOrNull(): T? = if (this is ApiResult<T>) data else null

inline fun <reified T: Any> Response<T>?.toApiResponse(): ApiResponse<T> = this?.run {
  when {
    isSuccessful -> {
      val body = body()
      if (body == null)
        ApiEmpty()
      else
        ApiResult(body)
    }
    else -> {
      ApiError(code(), errorBody()?.string() ?: code().toString())
    }
  }
} ?: ApiEmpty()
