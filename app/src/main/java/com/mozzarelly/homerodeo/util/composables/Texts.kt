package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration

@Composable
fun BodyText(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
  italic: Boolean = false,
  strikethrough: Boolean = false,
  underlined: Boolean = false,
  maxLines: Int? = null,
  align: TextAlign = TextAlign.Center,
  color: Color = MaterialTheme.colorScheme.onBackground
){
  Text(
    text = text,
    style = MaterialTheme.typography.bodyMedium.copy(
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
      fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
      textDecoration = when {
        underlined -> TextDecoration.Underline
        strikethrough -> TextDecoration.LineThrough
        else -> TextDecoration.None
      }
    ),
    textAlign = align,
    color = color,
    maxLines = maxLines ?: Int.MAX_VALUE,
    modifier = modifier
  )
}

@Composable
fun SmallBodyText(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
  underlined: Boolean = false,
  align: TextAlign = TextAlign.Center,
  maxLines: Int? = null,
  color: Color = MaterialTheme.colorScheme.onBackground
){
  Text(
    text = text,
    style = MaterialTheme.typography.bodySmall.copy(
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
      textDecoration = if (underlined) TextDecoration.Underline else TextDecoration.None
    ),
    textAlign = align,
    color = color,
    maxLines = maxLines ?: Int.MAX_VALUE,
    modifier = modifier
  )
}

@Composable
fun LargeTitleText(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
  underlined: Boolean = false,
  align: TextAlign = TextAlign.Center,
  maxLines: Int? = null,
  color: Color = MaterialTheme.colorScheme.onBackground
){
  Text(
    text = text,
    style = MaterialTheme.typography.titleLarge.copy(
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
      textDecoration = if (underlined) TextDecoration.Underline else TextDecoration.None
    ),
    textAlign = align,
    color = color,
    maxLines = maxLines ?: Int.MAX_VALUE,
    modifier = modifier
  )
}

@Composable
fun TitleText(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
  underlined: Boolean = false,
  align: TextAlign = TextAlign.Center,
  maxLines: Int? = null,
  color: Color = MaterialTheme.colorScheme.onBackground
){
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium.copy(
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
      textDecoration = if (underlined) TextDecoration.Underline else TextDecoration.None
    ),
    textAlign = align,
    color = color,
    maxLines = maxLines ?: Int.MAX_VALUE,
    modifier = modifier
  )
}

@Composable
fun SubtitleText(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
  underlined: Boolean = false,
  align: TextAlign = TextAlign.Center,
  maxLines: Int? = null,
  color: Color = MaterialTheme.colorScheme.onBackground
){
  Text(
    text = text,
    style = MaterialTheme.typography.titleSmall.copy(
      fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
      textDecoration = if (underlined) TextDecoration.Underline else TextDecoration.None
    ),
    textAlign = align,
    color = color,
    maxLines = maxLines ?: Int.MAX_VALUE,
    modifier = modifier
  )
}

@Composable
fun CenteredTitle(
  text: String,
  modifier: Modifier = Modifier,
  bold: Boolean = false,
){
  TitleText(text, bold = bold, align = TextAlign.Center, modifier = modifier.fillMaxWidth())
}