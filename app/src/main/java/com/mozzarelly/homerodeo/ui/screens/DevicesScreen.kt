package com.mozzarelly.homerodeo.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mozzarelly.homerodeo.ui.vm.DevicesViewModel

@Preview
@Composable
fun DevicesScreen(
  viewModel: DevicesViewModel = hiltViewModel(),
  modifier: Modifier = Modifier,
) {
  Column(
    modifier = modifier
  ){
    TODO()
  }
}
