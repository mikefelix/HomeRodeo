package com.mozzarelly.homerodeo.util.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mozzarelly.homerodeo.util.applyIf
import com.mozzarelly.homerodeo.util.notNullAnd
import com.mozzarelly.rodeo.ui.composables.ScrollingPicker
import kotlinx.coroutines.flow.distinctUntilChanged
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun NumberPickerUi(start: Float,
                   allowDecimal: Boolean,
                   modifier: Modifier = Modifier,
                   min: Float = 0f,
                   max: Float = 999f,
                   onCancel: () -> Unit,
                   onSubmit: (Float) -> Unit
){
  val state = remember { mutableStateOf<Float?>(start) }
  val valid by remember { derivedStateOf { state.value.notNullAnd { it in (min..max) } } }

  Column(modifier) {
    NumberPicker(state, start, allowDecimal, min = min, max = max, modifier = Modifier.fillMaxWidth())

//    BodyText(state.floatValue.toString())

    Row(
      horizontalArrangement = spacedBy(8.dp),
      modifier = Modifier.padding(16.dp)
    ) {
      SecondaryButton("Cancel", modifier = Modifier.weight(1f), onClick = onCancel)
      PrimaryButton(if (valid) "Confirm" else "Invalid!", enabled = valid, modifier = Modifier.weight(1f)) {
        onSubmit(state.value!!)
      }
    }
  }}

private val format = DecimalFormat.getInstance().apply {
  maximumFractionDigits = 1
  minimumFractionDigits = 1
  minimumIntegerDigits = 3
  roundingMode = RoundingMode.HALF_UP
  isGroupingUsed = false
}

@Composable
fun NumberPicker(state: MutableState<Float?>,
                 start: Float,
                 allowDecimal: Boolean,
                 modifier: Modifier = Modifier,
                 min: Float = 1f,
                 max: Float = 999f,
                 debug: Boolean = false
) {
  require(start in min..max) { "Start must be within $min..$max" }
  require(max < 1000) { "This component only supports numbers up to 999." }

  val numString = format.format(start).takeIf { it.length == 5} ?: error("Expected a string of length 5 from NumberFormat, got ${format.format(start)}")
  val hundreds = numString[0].digitToInt()
  val tens = numString[1].digitToInt()
  val ones = numString[2].digitToInt()
  val tenths = numString[4].digitToInt()

  val hundredsState = remember { mutableIntStateOf(hundreds) }
  val tensState = remember { mutableIntStateOf(tens) }
  val onesState = remember { mutableIntStateOf(ones) }
  val tenthsState = remember { mutableIntStateOf(tenths) }

  val value by remember { derivedStateOf {
    val string = if (allowDecimal)
      "${hundredsState.intValue}${tensState.intValue}${onesState.intValue}.${tenthsState.intValue}"
    else
      "${hundredsState.intValue}${tensState.intValue}${onesState.intValue}"

    val num = format.parse(string) ?: error("Couldn't parse $string")
    num.toFloat()
  } }

  val valid = remember(value) { value in min..max }

  LaunchedEffect("value") {
    snapshotFlow { value.takeIf { valid } }
      .distinctUntilChanged()
      .collect {
        state.value = it
      }
  }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .padding(16.dp)
      .applyIf(!valid) { border(1.dp, Color.Red, RoundedCornerShape(4.dp)) }
  ) {
    Row(
      horizontalArrangement = spacedBy(16.dp),
      modifier = Modifier.padding(32.dp)
    ) {
      if (max >= 100) {
        ScrollingPicker(
          items = (0..(max.toInt() / 100)).map { it.toString() },
          modifier = Modifier.width(32.dp),
          startIndex = hundreds,
          selectionState = hundredsState,
          textStyle = MaterialTheme.typography.titleLarge
        )
      }

      if (max >= 10) {
        ScrollingPicker(
          items = (0..9).map { it.toString() },
          modifier = Modifier.width(32.dp),
          startIndex = tens,
          selectionState = tensState,
          textStyle = MaterialTheme.typography.titleLarge
        )
      }

      ScrollingPicker(
        items = (0..9).map { it.toString() },
        modifier = Modifier.width(32.dp),
        startIndex = ones,
        selectionState = onesState,
        textStyle = MaterialTheme.typography.titleLarge
      )

      if (allowDecimal){
        TitleText(".", modifier = Modifier.align(Alignment.CenterVertically))

        ScrollingPicker(
          items = (0..9).map { it.toString() },
          modifier = Modifier.width(32.dp),
          startIndex = tenths,
          selectionState = tenthsState,
          textStyle = MaterialTheme.typography.titleLarge
        )
      }

    }

    if (debug){
      Row {
        BodyText(state.value.toString())
      }
    }
  }
}

@Composable
@Preview(showBackground = true)
private fun DecimalNumberPickerPreview(){
  NumberPickerUi(start = 123f, allowDecimal = true, onCancel = { }, onSubmit = {})
}

@Composable
@Preview(showBackground = true)
private fun NumberPickerPreview(){
  NumberPickerUi(start = 123f, allowDecimal = false, onCancel = { }, onSubmit = {})
}