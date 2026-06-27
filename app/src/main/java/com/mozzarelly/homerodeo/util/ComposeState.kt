package com.mozzarelly.homerodeo.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.CoroutineContext


@Suppress("StateFlowValueCalledInComposition")
@Composable
fun <T> StateFlow<T>.collectAsMutableState(): MutableState<T> = collectAsMutableState(value)


@Composable
fun <T : R, R> Flow<T>.collectAsMutableState(
  initial: R,
): MutableState<R> =
  produceMutableState(initial, this) {
    collect { value = it }
  }

@Composable
fun <T> produceMutableState(
  initialValue: T,
  key1: Any?,
  producer: suspend ProduceStateScope<T>.() -> Unit,
): MutableState<T> {
  val result = remember { mutableStateOf(initialValue) }
  LaunchedEffect(key1) { ProduceMutableStateScopeImpl(result, coroutineContext).producer() }
  return result
}

private class ProduceMutableStateScopeImpl<T>(
  state: MutableState<T>,
  override val coroutineContext: CoroutineContext,
) : ProduceStateScope<T>, MutableState<T> by state {

  override suspend fun awaitDispose(onDispose: () -> Unit): Nothing {
    try {
      suspendCancellableCoroutine<Nothing> {}
    } finally {
      onDispose()
    }
  }
}