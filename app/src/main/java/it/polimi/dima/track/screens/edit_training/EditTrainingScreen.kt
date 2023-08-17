package it.polimi.dima.track.screens.edit_training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import it.polimi.dima.track.R
import it.polimi.dima.track.common.composable.ActionToolbar
import it.polimi.dima.track.common.composable.BasicField
import it.polimi.dima.track.common.composable.CardSelector
import it.polimi.dima.track.common.composable.DialogCancelButton
import it.polimi.dima.track.common.composable.DialogConfirmButton
import it.polimi.dima.track.common.composable.MultiLineField
import it.polimi.dima.track.common.composable.RegularCardEditor
import it.polimi.dima.track.common.composable.TimePickerDialog
import it.polimi.dima.track.common.composable.TrainingStepsListBox
import it.polimi.dima.track.common.ext.card
import it.polimi.dima.track.common.ext.fieldModifier
import it.polimi.dima.track.common.ext.hasDueDate
import it.polimi.dima.track.common.ext.hasDueTime
import it.polimi.dima.track.common.ext.bigSpacer
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
      .fillMaxHeight(),
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

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
    ) {
      Spacer(modifier = Modifier.bigSpacer())

      val fieldModifier = Modifier.fieldModifier()
      BasicField(R.string.title, training.title, viewModel::onTitleChange, fieldModifier)
      BasicField(
        R.string.description,
        training.description,
        viewModel::onDescriptionChange,
        fieldModifier
      )
      MultiLineField(R.string.notes, training.notes, viewModel::onNotesChange, fieldModifier)


      Spacer(modifier = Modifier.bigSpacer())
      CardEditors(training, viewModel::onDateChange, viewModel::onTimeChange)
      CardSelectors(training, viewModel::onTypeChange, viewModel::onFavoriteToggle)

      Spacer(modifier = Modifier.bigSpacer())

      TrainingStepsListBox(
        training = training,
        filling = false,
        onEditSteps = { viewModel.onEditSteps(openScreen) }
      )
    }
  }
}

@Composable
private fun CardEditors(
  training: Training,
  onDateChange: (Long) -> Unit,
  onTimeChange: (Int, Int) -> Unit
) {
  /*
   * rememberSaveable is used to save the state of the dialog during configuration changes.
   */
  val openDateDialog = rememberSaveable { mutableStateOf(false) }
  val openTimeDialog = rememberSaveable { mutableStateOf(false) }
  RegularCardEditor(
    R.string.date,
    Icons.Filled.CalendarMonth,
    training.dueDateString,
    Modifier.card()
  ) {
    openDateDialog.value = true
  }

  RegularCardEditor(
    R.string.time,
    Icons.Filled.AccessTime,
    training.dueTimeString,
    Modifier.card()
  ) {
    openTimeDialog.value = true
  }

  if (openDateDialog.value) {
    EmbeddedDatePicker(
      training = training,
      onClose = { openDateDialog.value = false },
      onDateChange = onDateChange
    )
  }

  if (openTimeDialog.value) {
    EmbeddedTimePicker(
      training = training,
      onClose = { openTimeDialog.value = false },
      onTimeChange = onTimeChange
    )
  }
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
      DialogConfirmButton(text = R.string.ok) {
        datePickerState.selectedDateMillis?.let { onDateChange(it) }
        onClose()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.cancel) {
        onClose()
      }
    }
  ) {
    DatePicker(state = datePickerState)
  }
}

@OptIn(ExperimentalMaterial3Api::class)
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

  val timePickerState = rememberTimePickerState(
    initialHour = initialHour,
    initialMinute = initialMinute,
    is24Hour = true
  )
  TimePickerDialog(
    onDismissRequest = {
      onClose()
    },
    confirmButton = {
      DialogConfirmButton(text = R.string.ok) {
        onTimeChange(timePickerState.hour, timePickerState.minute)
        onClose()
      }
    },
    dismissButton = {
      DialogCancelButton(text = R.string.cancel) {
        onClose()
      }
    }
  ) {
    TimePicker(
      state = timePickerState
    )
  }
}