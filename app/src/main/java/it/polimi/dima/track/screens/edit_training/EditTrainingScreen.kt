package it.polimi.dima.track.screens.edit_training

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.BasicField
import it.polimi.dima.track.common.composable.CardSelector
import it.polimi.dima.track.common.composable.RegularCardEditor
import it.polimi.dima.track.common.composable.TrainingStepsListBox
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.Type

@Composable
fun EditTrainingScreen(
  openScreen: (String) -> Unit,
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: EditTrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) {
    viewModel.initialize(trainingId)
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .fillMaxHeight()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    ActionToolbar(
      title = R.string.edit_training,
      modifier = Modifier.toolbarActions(),
      startActionIcon = Icons.Default.Close,
      startActionDescription = R.string.close,
      startAction = { viewModel.onCancelClick(popUpScreen) },
      endActionIcon = Icons.Default.Check,
      endActionDescription = R.string.confirm,
      endAction = { viewModel.onDoneClick(popUpScreen) }
    )

    Spacer(modifier = Modifier.spacer())

    val fieldModifier = Modifier.fieldModifier()
    BasicField(R.string.title, training.title, viewModel::onTitleChange, fieldModifier)
    BasicField(
      R.string.description,
      training.description,
      viewModel::onDescriptionChange,
      fieldModifier
    )

    Spacer(modifier = Modifier.spacer())
    CardEditors(training, viewModel::onDateChange, viewModel::onTimeChange)
    CardSelectors(training, viewModel::onTypeChange, viewModel::onFavoriteToggle)

    Spacer(modifier = Modifier.spacer())

    TrainingStepsListBox(
      training = training,
      filling = false,
      openScreen = openScreen
    )
  }
}

@Composable
private fun CardEditors(
  training: Training,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit
) {
  val activity = LocalContext.current as AppCompatActivity

  /*
   * rememberSaveable is used to save the state of the dialog during configuration changes.
   */
  val openDateDialog = rememberSaveable { mutableStateOf(false) }
  val openTimeDialog = rememberSaveable { mutableStateOf(false) }
  RegularCardEditor(
    R.string.date,
    R.drawable.ic_calendar,
    training.dueDateString,
    Modifier.card()
  ) {
    openDateDialog.value = true
  }

  RegularCardEditor(R.string.time, R.drawable.ic_clock, training.dueTimeString, Modifier.card()) {
    showTimePicker(activity, training, onTimeChange)
    // openTimeDialog.value = true
  }

  if (openDateDialog.value) {
    EmbeddedDatePicker(
      training = training,
      onClose = { openDateDialog.value = false },
      onDateChange = onDateChange
    )
  }

  /*if (openTimeDialog.value) {
    EmbeddedTimePicker(
      training = training,
      onClose = { openTimeDialog.value = false },
      onTimeChange = onTimeChange
    )
  }*/
}

@Composable
private fun CardSelectors(
  training: Training,
  onTypeChange: (String) -> Unit,
  onFavouriteToggle: (String) -> Unit
) {
  val typeSelection = Type.getByName(training.type).name
  CardSelector(R.string.type, Type.getOptions(), typeSelection, Modifier.card()) { newValue ->
    onTypeChange(newValue)
  }

  val favouriteSelection = EditFavouriteOption.getByCheckedState(training.favorite).name
  CardSelector(
    R.string.favorite,
    EditFavouriteOption.getOptions(),
    favouriteSelection,
    Modifier.card()
  ) { newValue
    ->
    onFavouriteToggle(newValue)
  }
}

// TODO non si adatta al layout orizzontale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmbeddedDatePicker(
  training: Training,
  onClose: () -> Unit,
  onDateChange: (Long) -> Unit
) {

  val initialDate = remember(training.hasDueDate()) {
    if (training.hasDueDate()) {
      training.dueDate!!.time
    } else {
      MaterialDatePicker.todayInUtcMilliseconds()
    }
  }

  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
  DatePickerDialog(
    onDismissRequest = {
      onClose()
    },
    confirmButton = {
      TextButton(
        onClick = {
          datePickerState.selectedDateMillis?.let { onDateChange(it) }
          onClose()
        },
        enabled = true
      ) {
        Text("OK")
      }
    },
    dismissButton = {
      TextButton(
        onClick = {
          onClose()
        }
      ) {
        Text("Cancel")
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}

/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmbeddedTimePicker(
  training: Training,
  onClose: () -> Unit,
  onTimeChange: (Int, Int) -> Unit
) {
  val initialHour = remember(training.hasDueTime()) {
    if (training.hasDueTime()) {
      training.dueTime!!["hour"]!!
    } else {
      0
    }
  }
  val initialMinute = remember(training.hasDueTime()) {
    if (training.hasDueTime()) {
      training.dueTime!!["minute"]!!
    } else {
      0
    }
  }

  val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
  /*TimePickerDialog(
    onCancel = { onClose() },
    onConfirm = {
      onTimeChange(timePickerState.hour, timePickerState.minute)
      onClose()
    },
  ) {
  }*/
  TimePicker(
    state = timePickerState
  )
}*/

private fun showTimePicker(
  activity: AppCompatActivity?,
  training: Training,
  onTimeChange: (Int, Int) -> Unit
) {
  var selectedHour = 0
  var selectedMinute = 0
  if (training.hasDueTime()) {
    selectedHour = training.dueTime!!["hour"]!!
    selectedMinute = training.dueTime["minute"]!!
  }
  val picker = MaterialTimePicker.Builder()
    .setTimeFormat(TimeFormat.CLOCK_24H)
    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
    .setHour(selectedHour)
    .setMinute(selectedMinute)
    .build()

  activity?.let {
    picker.show(it.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { onTimeChange(picker.hour, picker.minute) }
  }
}