package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.PrimaryButton(
  text: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  color: Color = MaterialTheme.colorScheme.primary,
  onClick: () -> Unit
) {
  OutlinedButton(
    border = BorderStroke(0.dp, MaterialTheme.colorScheme.secondary),
    onClick = onClick,
    enabled = enabled,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp)
      .weight(1f),
    colors = ButtonDefaults.buttonColors(
      contentColor = MaterialTheme.colorScheme.background,
      containerColor = color,
    ),
  ) {
    Text(text, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
  }
}

@Composable
fun RowScope.SecondaryButton(
  text: String,
  modifier: Modifier = Modifier,
  color: Color = MaterialTheme.colorScheme.background,
  onClick: () -> Unit
) {
  OutlinedButton(
    border = BorderStroke(0.dp, MaterialTheme.colorScheme.secondary),
    onClick = onClick,
    modifier = modifier
      .defaultMinSize(minHeight = 48.dp)
      .weight(1f),
    colors = ButtonDefaults.buttonColors(
      contentColor = MaterialTheme.colorScheme.background,
      containerColor = color,
    ),
  ) {
    Text(text,
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.secondary
    )
  }
}
