package it.polimi.dima.track.screens.edit_training

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
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
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.common.ext.spacer
import it.polimi.dima.track.common.ext.toolbarActions
import it.polimi.dima.track.model.Priority
import it.polimi.dima.track.model.Training

@Composable
fun EditTrainingScreen(
  popUpScreen: () -> Unit,
  trainingId: String,
  modifier: Modifier = Modifier,
  viewModel: EditTrainingViewModel = hiltViewModel()
) {
  val training by viewModel.training

  LaunchedEffect(Unit) { viewModel.initialize(trainingId) }

  Column(
    modifier = modifier.fillMaxWidth().fillMaxHeight().verticalScroll(rememberScrollState()),
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
    BasicField(R.string.description, training.description, viewModel::onDescriptionChange, fieldModifier)

    Spacer(modifier = Modifier.spacer())
    CardEditors(training, viewModel::onDateChange, viewModel::onTimeChange)
    CardSelectors(training, viewModel::onPriorityChange, viewModel::onFlagToggle)

    Spacer(modifier = Modifier.spacer())
  }
}

@Composable
private fun CardEditors(
  training: Training,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit
) {
  val activity = LocalContext.current as AppCompatActivity

  RegularCardEditor(R.string.date, R.drawable.ic_calendar, training.dueDateString, Modifier.card()) {
    showDatePicker(activity, training, onDateChange)
  }

  RegularCardEditor(R.string.time, R.drawable.ic_clock, training.dueTimeString, Modifier.card()) {
    showTimePicker(activity, training, onTimeChange)
  }
}

@Composable
private fun CardSelectors(
  training: Training,
  onPriorityChange: (String) -> Unit,
  onFlagToggle: (String) -> Unit
) {
  val prioritySelection = Priority.getByName(training.priority).name
  CardSelector(R.string.priority, Priority.getOptions(), prioritySelection, Modifier.card()) {
    newValue ->
    onPriorityChange(newValue)
  }

  val flagSelection = EditFlagOption.getByCheckedState(training.flag).name
  CardSelector(R.string.flag, EditFlagOption.getOptions(), flagSelection, Modifier.card()) { newValue
    ->
    onFlagToggle(newValue)
  }
}

private fun showDatePicker(activity: AppCompatActivity?, training: Training, onDateChange: (Long) -> Unit) {
  var selectedDate = MaterialDatePicker.todayInUtcMilliseconds()
  if (training.hasDueDate()) {
    selectedDate = training.dueDate!!.time
  }
  val picker = MaterialDatePicker.Builder.datePicker()
    .setSelection(selectedDate)
    .build()

  activity?.let {
    picker.show(it.supportFragmentManager, picker.toString())
    picker.addOnPositiveButtonClickListener { timeInMillis -> onDateChange(timeInMillis) }
  }
}

private fun showTimePicker(activity: AppCompatActivity?, training: Training, onTimeChange: (Int, Int) -> Unit) {
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
