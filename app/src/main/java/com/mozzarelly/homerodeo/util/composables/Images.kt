package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun LocalImage(resource: Int, description: String,
               modifier: Modifier = Modifier,
               scale: ContentScale = ContentScale.Fit,
               alpha: Float = DefaultAlpha,
               tint: Color? = null,
               alignment: Alignment = Center
){
  Image(
    painter = painterResource(resource),
    contentScale = scale,
    alpha = alpha,
    alignment = alignment,
    colorFilter = tint?.let { ColorFilter.tint(it) },
    contentDescription = description,
    modifier = modifier
  )
}

