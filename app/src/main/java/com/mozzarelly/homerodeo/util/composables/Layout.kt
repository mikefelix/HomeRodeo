package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
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
fun <U: Any> FullSizeUi(
  state: UiState<U>,
  onRetry: () -> Unit,
  errorContent: (@Composable (Throwable) -> Unit)? = null,
  content: @Composable (U) -> Unit
){
  Ui(state,
    modifier = Modifier
      .fillMaxSize(),
    onRetry = onRetry,
    errorContent = errorContent,
    content = content)
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun SuccessPreview(){
  val state = UiState("The content", loading = false)
  FullSizeUi(state, onRetry = {}) {
    TitleText(it)
  }
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun LoadingPreview(){
  val state = UiState("The content", loading = true)
  FullSizeUi(state, onRetry = {}) {
    TitleText(it)
  }
}

@Composable
@Preview(showBackground = true, heightDp = 300)
private fun ErrorPreview(){
  val state: UiState<String> = UiState.error<String>(Exception("The error"), loading = true)
  FullSizeUi(state, onRetry = {}) {
    TitleText(it)
  }
}