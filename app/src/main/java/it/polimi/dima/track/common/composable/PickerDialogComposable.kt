package it.polimi.dima.track.common.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polimi.dima.track.R
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.model.TrainingStep
import it.polimi.dima.track.model.TrainingStep.PaceUnit.Companion.MIN_KM

@Composable
fun TimeSelectionDialog(
  title: String = "Duration time (h:m:s)",
  onDismissRequest: () -> Unit,
  onConfirm: (Boolean) -> Unit,
  durationSelection: Int,
  centsSelectable: Boolean = false,
  centsSelection: Int = 0,
  hourPickerState: PickerState,
  minutePickerState: PickerState,
  secondPickerState: PickerState,
  centsPickerState: PickerState = PickerState()
) {
  val displayCents = rememberSaveable { mutableStateOf(centsSelection != 0) }

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = title)
    },
    text = {
      Surface(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          if (centsSelectable) {
            SegmentedControl(
              items = listOf("HH:MM:SS", "MM:SS.CC"),
              defaultSelectedItemIndex = if (centsSelection == 0) 0 else 1,
              cornerRadius = 50,
              color = MaterialTheme.colorScheme.primary,
              onItemSelection = { displayCents.value = it == 1 }
            )

            Spacer(modifier = Modifier.spacer())
          }

          if (displayCents.value) {
            TimeSelectionDialogContent(
              durationSelection = durationSelection,
              centsSelection = centsSelection,
              minutePickerState = minutePickerState,
              secondPickerState = secondPickerState,
              centsPickerState = centsPickerState,
              centsSelectable = true
            )
          } else {
            TimeSelectionDialogContent(
              durationSelection = durationSelection,
              hourPickerState = hourPickerState,
              minutePickerState = minutePickerState,
              secondPickerState = secondPickerState,
              centsSelectable = false
            )
          }
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm(displayCents.value)
      }
    },
    dismissButton = {
      if (centsSelectable) {
        DialogCancelButton(text = R.string.clear) {
          hourPickerState.selectedItem = "0"
          minutePickerState.selectedItem = "00"
          secondPickerState.selectedItem = "00"
          centsPickerState.selectedItem = "00"
          onConfirm(displayCents.value)
        }
      }

      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    },
  )
}

@Composable
fun TimeSelectionDialogContent(
  durationSelection: Int,
  centsSelection: Int = 0,
  hourPickerState: PickerState = PickerState(),
  minutePickerState: PickerState,
  secondPickerState: PickerState,
  centsPickerState: PickerState = PickerState(),
  centsSelectable: Boolean = false
) {
  Surface(modifier = Modifier.height(160.dp)) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {

      Row(modifier = Modifier.fillMaxWidth()) {
        if (!centsSelectable) {
          NumberPicker(
            state = hourPickerState,
            items = remember { (0..23).map { it.toString() } },
            modifier = Modifier.weight(1f),
            visibleItemsCount = 3,
            startIndex = durationSelection / 3600,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
        NumberPicker(
          state = minutePickerState,
          items = remember { (0..59).map { it.toString().padStart(2, '0') } },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = durationSelection / 60 % 60,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = secondPickerState,
          items = remember { (0..59).map { it.toString().padStart(2, '0') } },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = durationSelection % 60,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        if (centsSelectable) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
          ) {
            Text(text = ".", fontSize = 24.sp)
          }
          NumberPicker(
            state = centsPickerState,
            items = remember { (0..99).map { it.toString().padStart(2, '0') } },
            visibleItemsCount = 3,
            modifier = Modifier.weight(1f),
            startIndex = centsSelection,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
      }
    }
  }
}

@Composable
fun PaceSelectionDialog(
  title: String = "Pace (m:s)",
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  paceSelection: Int,
  paceUnitSelection: String,
  minutePickerState: PickerState,
  secondPickerState: PickerState,
  paceUnitPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = title)
    },
    text = {
      Surface(modifier = Modifier.height(160.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {
          Row(modifier = Modifier.fillMaxWidth()) {
            NumberPicker(
              state = minutePickerState,
              items = remember { (0..59).map { it.toString().padStart(2, '0') } },
              visibleItemsCount = 3,
              modifier = Modifier.weight(1f),
              startIndex = paceSelection / 60 % 60,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
            NumberPicker(
              state = secondPickerState,
              items = remember { (0..59).map { it.toString().padStart(2, '0') } },
              visibleItemsCount = 3,
              modifier = Modifier.weight(1f),
              startIndex = paceSelection % 60,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
            NumberPicker(
              state = paceUnitPickerState,
              items = remember { TrainingStep.PaceUnit.getOptions() },
              visibleItemsCount = 3,
              modifier = Modifier.weight(2f),
              startIndex = if (paceUnitSelection == MIN_KM) 0 else 1,
              textModifier = Modifier.padding(8.dp),
              textStyle = TextStyle(fontSize = 24.sp)
            )
          }
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.clear) {
        minutePickerState.selectedItem = "00"
        secondPickerState.selectedItem = "00"
        paceUnitPickerState.selectedItem = MIN_KM
        onConfirm()
      }

      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    },
  )
}

@Composable
fun DistanceSelectionDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  distanceSelection: Int,
  distanceUnitSelection: String,
  mostSignificantDigitsPickerState: PickerState,
  leastSignificantDigitsPickerState: PickerState,
  measurementPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = "Distance")
    },
    text = {
      DistanceSelectionDialogContent(
        distanceSelection,
        distanceUnitSelection,
        mostSignificantDigitsPickerState,
        leastSignificantDigitsPickerState,
        measurementPickerState
      )
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
}

@Composable
fun DistanceSelectionDialogContent(
  distanceSelection: Int,
  distanceUnitSelection: String,
  mostSignificantDigitsPickerState: PickerState,
  leastSignificantDigitsPickerState: PickerState,
  measurementPickerState: PickerState
) {
  Surface(modifier = Modifier.height(160.dp)) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {

      Row(modifier = Modifier.fillMaxWidth()) {
        NumberPicker(
          state = mostSignificantDigitsPickerState,
          items = remember { (0..99).map { it.toString() } },
          modifier = Modifier.weight(1f),
          visibleItemsCount = 3,
          startIndex = distanceSelection / 100,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = leastSignificantDigitsPickerState,
          items = remember { (0..99).map { it.toString().padStart(2, '0') } },
          modifier = Modifier.weight(1f),
          visibleItemsCount = 3,
          startIndex = distanceSelection % 100,
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
        NumberPicker(
          state = measurementPickerState,
          items = remember { listOf("m", "km", "mi") },
          visibleItemsCount = 3,
          modifier = Modifier.weight(1f),
          startIndex = when (distanceUnitSelection) {
            "km" -> 1
            "mi" -> 2
            else -> 0
          },
          textModifier = Modifier.padding(8.dp),
          textStyle = TextStyle(fontSize = 24.sp)
        )
      }
    }
  }
}

@Composable
fun RepetitionsSelectionDialog(
  onDismissRequest: () -> Unit,
  onConfirm: () -> Unit,
  currentRepetitions: Int,
  repetitionsPickerState: PickerState
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = "Repetition number")
    },
    text = {
      Surface(modifier = Modifier.height(160.dp)) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxSize()
        ) {

          val values = remember { (2..50).map { it.toString() } }

          NumberPicker(
            state = repetitionsPickerState,
            items = values,
            visibleItemsCount = 3,
            startIndex = currentRepetitions - 2,
            textModifier = Modifier.padding(8.dp),
            textStyle = TextStyle(fontSize = 24.sp)
          )
        }
      }
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
}

@Composable
fun RecoverSelectionDialog(
  title: String,
  onDismissRequest: () -> Unit,
  onConfirm: (String, Int, Int, String) -> Unit,
  currentRecoverType: String,
  currentRecoverDuration: Int,
  currentRecoverDistance: Int,
  currentRecoverDistanceUnit: String,
) {
  val recoverType = rememberSaveable { mutableStateOf(currentRecoverType) }
  val durationHourPickerState = rememberPickerState()
  val durationMinutePickerState = rememberPickerState()
  val durationSecondPickerState = rememberPickerState()
  val distanceMostSignificantDigitPickerState = rememberPickerState()
  val distanceLeastSignificantDigitsPickerState = rememberPickerState()
  val distanceMeasurementPickerState = rememberPickerState()

  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(text = title)
    },
    text = {
      RecoverSelectionDialogContent(
        currentRecoverType,
        recoverType,
        currentRecoverDuration,
        durationHourPickerState,
        durationMinutePickerState,
        durationSecondPickerState,
        currentRecoverDistance,
        currentRecoverDistanceUnit,
        distanceMostSignificantDigitPickerState,
        distanceLeastSignificantDigitsPickerState,
        distanceMeasurementPickerState
      )
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.confirm) {
        onConfirm(
          recoverType.value,
          if (recoverType.value == TrainingStep.DurationType.TIME)
            durationHourPickerState.selectedItem.toInt() * 3600 + durationMinutePickerState.selectedItem.toInt() * 60 + durationSecondPickerState.selectedItem.toInt()
          else currentRecoverDuration,
          if (recoverType.value == TrainingStep.DurationType.DISTANCE)
            distanceMostSignificantDigitPickerState.selectedItem.toInt() * 100 + distanceLeastSignificantDigitsPickerState.selectedItem.toInt()
          else currentRecoverDistance,
          if (recoverType.value == TrainingStep.DurationType.DISTANCE)
            distanceMeasurementPickerState.selectedItem
          else currentRecoverDistanceUnit
        )
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.dismiss) {
        onDismissRequest()
      }
    }
  )
}

@Composable
private fun RecoverSelectionDialogContent(
  currentRecoverType: String,
  recoverType: MutableState<String>,
  currentRecoverDuration: Int,
  durationHourPickerState: PickerState,
  durationMinutePickerState: PickerState,
  durationSecondPickerState: PickerState,
  currentRecoverDistance: Int,
  currentRecoverDistanceUnit: String,
  distanceMostSignificantDigitPickerState: PickerState,
  distanceLeastSignificantDigitsPickerState: PickerState,
  distanceMeasurementPickerState: PickerState
) {
  Surface(modifier = Modifier.height(220.dp)) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxSize()
    ) {
      SegmentedControl(
        items = TrainingStep.DurationType.getOptions(),
        defaultSelectedItemIndex = if (currentRecoverType == TrainingStep.DurationType.TIME) 0 else 1,
        cornerRadius = 50,
        color = MaterialTheme.colorScheme.primary,
        onItemSelection = { index ->
          recoverType.value =
            if (index == 0) TrainingStep.DurationType.TIME else TrainingStep.DurationType.DISTANCE
        }
      )

      Spacer(modifier = Modifier.spacer())

      if (recoverType.value == TrainingStep.DurationType.TIME) {
        TimeSelectionDialogContent(
          durationSelection = currentRecoverDuration,
          hourPickerState = durationHourPickerState,
          minutePickerState = durationMinutePickerState,
          secondPickerState = durationSecondPickerState,
          centsSelectable = false
        )
      } else {
        DistanceSelectionDialogContent(
          distanceSelection = currentRecoverDistance,
          distanceUnitSelection = currentRecoverDistanceUnit,
          mostSignificantDigitsPickerState = distanceMostSignificantDigitPickerState,
          leastSignificantDigitsPickerState = distanceLeastSignificantDigitsPickerState,
          measurementPickerState = distanceMeasurementPickerState
        )
      }
    }
  }
}