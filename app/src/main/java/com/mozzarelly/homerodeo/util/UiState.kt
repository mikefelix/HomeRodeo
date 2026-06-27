package com.mozzarelly.homerodeo.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

data class UiState<S>(val state: S? = null, val error: Throwable? = null, val loading: Boolean = false){
  fun markLoading(): UiState<S> = copy(loading = true)

  fun withError(e: Throwable) = copy(error = e, loading = false)

  fun <T> map(func: (S) -> T) = UiState(state?.let { func(it) }, error, loading)

  companion object {
    fun <S> error(e: Throwable, loading: Boolean? = null) = UiState<S>(error = e, loading = loading
      ?: false)
    fun <S> error(message: String, loading: Boolean? = null) = UiState<S>(error = Exception(message), loading = loading
      ?: false)
    fun <S> success(state: S, loading: Boolean? = null) = UiState(state = state, loading = loading
      ?: false)
    fun <S> loading() = UiState<S>(loading = true)
  }
}

fun <R: Any> ApiResponse<R>.toUiState(
  genericError: String = "There was an error retrieving the data."
): UiState<R> {
  return when (this){
    is ApiUnknown -> UiState(loading = true)
    is ApiResult -> UiState.success(data)
    is ApiException -> UiState.error(e)
    else -> UiState.error(genericError)
  }
}

fun <T: Any, A: ApiResponse<T>> Flow<A>.toUiStateFlow(scope: CoroutineScope): StateFlow<UiState<T>> =
  map { it.toUiState() }
    .stateIn(scope, SharingStarted.WhileSubscribed(3000), UiState.loading())
