@file:OptIn(ExperimentalMaterial3Api::class)

package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mozzarelly.homerodeo.util.UiState

@Composable
fun <U: Any> Ui(
  state: UiState<U>,
  modifier: Modifier = Modifier,
  onRetry: () -> Unit,
  errorContent: (@Composable (Throwable) -> Unit)? = null,
  loadingContent: (@Composable () -> Unit) = {
    CenteredProgressIndicator()
  },
  content: @Composable (U) -> Unit
) {
  val (data, error, loading) = state

  Box(modifier = modifier) {
    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
    ) {
      if (loading) {
        loadingContent()
      }
    }

    Column(modifier = Modifier) {
      error?.let {
        if (errorContent != null) {
          errorContent(it)
        }
        else {
          BodyText(
            "Sorry, there was an error loading the content.",
            modifier = Modifier
              .fillMaxWidth()
          )

          Row {
            PrimaryButton("Retry") { onRetry() }
          }
        }
      }

      data?.let {
        content(it)
      }
    }
  }
}

@Composable
private fun CenteredProgressIndicator(){
  Column(
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier.fillMaxSize()
  ) {
    CircularProgressIndicator()
  }
}

@Composable
fun <U: Any, S> FullSizeUiWithSheet(
  state: UiState<U>,
  onRetry: () -> Unit,
  sheetState: MutableState<S?> = remember { mutableStateOf(null) },
  errorContent: (@Composable (Throwable) -> Unit)? = null,
  sheetContent: @Composable (S) -> Unit,
  content: @Composable (U) -> Unit,
){
  Ui(state,
    modifier = Modifier
      .systemBarsPadding()
      .fillMaxSize(),
    onRetry = onRetry,
    errorContent = errorContent,
    content = content)

  sheetState.value?.let {
    ModalBottomSheet(
      onDismissRequest = { sheetState.value = null },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
      sheetContent(it)
    }
  }
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun SuccessPreview(){
  val state = UiState("The content", loading = false)
  FullSizeUiWithSheet(
    state = state,
    sheetState = remember { mutableStateOf(false) },
    onRetry = {},
    sheetContent = {}
  ) {
    TitleText(it)
  }
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun LoadingPreview(){
  val state = UiState("The content", loading = true)
  FullSizeUiWithSheet(
    state = state,
    sheetState = remember { mutableStateOf(false) },
    onRetry = {},
    sheetContent = {}
  ) {
    TitleText(it)
  }
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun ErrorPreview(){
  val state: UiState<String> = UiState.error(Exception("The error"), loading = true)
  FullSizeUiWithSheet(
    state = state,
    sheetState = remember { mutableStateOf(false) },
    onRetry = {},
    sheetContent = {}
  ) {
    TitleText(it)
  }
}