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

class UiStateFlow<T: Any>(backingFlow: Flow<ApiResponse<T>>, scope: CoroutineScope): StateFlow<UiState<T>> {
  private val manualFlow = MutableStateFlow<UiState<T>>(UiState.loading())
  private val combinedFlow = merge(backingFlow.map { it.toUiState() }, manualFlow)
    .stateIn(scope, SharingStarted.WhileSubscribed(3000), UiState.loading())

  override val replayCache: List<UiState<T>>
    get() = combinedFlow.replayCache

  override suspend fun collect(collector: FlowCollector<UiState<T>>): Nothing {
    combinedFlow.collect(collector)
  }

  override val value: UiState<T>
    get() = combinedFlow.value

  fun emitLoading(){
    manualFlow.value = value.markLoading()
  }

  fun emit(t: () -> T) {
    manualFlow.value = UiState(t())
  }
}

fun <T: Any, A: ApiResponse<T>> Flow<A>.toUiStateFlow(scope: CoroutineScope): UiStateFlow<T> = UiStateFlow(this, scope)
//  map { it.toUiState() }
//    .stateIn(scope, SharingStarted.WhileSubscribed(3000), UiState.loading())
