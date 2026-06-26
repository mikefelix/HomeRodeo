package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun TitleBar(
) {
  Row(verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .height(48.dp)
      .background(MaterialTheme.colorScheme.primary)
  ) {
    TitleText(
      text = "Rodeo",
      color = MaterialTheme.colorScheme.onPrimary,
      align = TextAlign.Start,
      modifier = Modifier
        .padding(horizontal = 16.dp)
        .weight(1f)
    )
  }
}