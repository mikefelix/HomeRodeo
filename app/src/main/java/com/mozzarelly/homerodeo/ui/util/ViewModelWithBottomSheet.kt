package com.mozzarelly.homerodeo.ui.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class ViewModelWithBottomSheet<S> : ViewModel() {
  private val _sheetState = MutableStateFlow<S?>(null)
  val sheetState: StateFlow<S?> = _sheetState

  fun showSheet(state: S) {
    _sheetState.value = state
  }

  fun hideSheet() {
    _sheetState.value = null
  }
}

interface ScreenWithBottomSheetActions<S> {
  fun showSheet(state: S)
  fun hideSheet()
}
