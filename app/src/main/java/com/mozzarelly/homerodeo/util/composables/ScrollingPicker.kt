package com.mozzarelly.rodeo.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> ScrollingPicker(
  items: List<T>,
  modifier: Modifier = Modifier,
  startIndex: Int = 0,
  selectionState: MutableState<Int> = remember { mutableIntStateOf(startIndex) },
  visibleItemsCount: Int = 3,
  itemHeight: Dp = 48.dp,
  textModifier: Modifier = Modifier,
  textStyle: TextStyle = LocalTextStyle.current,
  dividerColor: Color = LocalContentColor.current,
) {
  if (items.isEmpty()) return
  
  val visibleItemsMiddle = visibleItemsCount / 2
  val listScrollCount = 1000 * items.size
  val listScrollMiddle = listScrollCount / 2
  val listStartIndex = listScrollMiddle - listScrollMiddle % items.size - visibleItemsMiddle + startIndex
  val listState = rememberLazyListState(initialFirstVisibleItemIndex = listStartIndex)
  val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

  val fadingEdgeGradient = remember {
    Brush.verticalGradient(
      0f to Color.Transparent,
      0.2f to Color.Black,
      0.8f to Color.Black,
      1f to Color.Transparent
    )
  }

  LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
      .map { (it + visibleItemsMiddle) % items.size }
      .distinctUntilChanged()
      .collect { selectionState.value = it }
  }

  LazyColumn(
    state = listState,
    flingBehavior = flingBehavior,
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .height(itemHeight * visibleItemsCount)
      .fadingEdge(fadingEdgeGradient)
  ) {
    items(listScrollCount) { index ->
      Box(
        modifier = Modifier
          .height(itemHeight)
          .fillMaxWidth(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = items[index % items.size].toString(),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = textStyle,
          color = LocalContentColor.current,
          textAlign = TextAlign.Center,
          modifier = textModifier
        )
      }
    }
  }
}

private fun Modifier.fadingEdge(brush: Brush) = this
  .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
  .drawWithContent {
    drawContent()
    drawRect(brush = brush, blendMode = BlendMode.DstIn)
  }
