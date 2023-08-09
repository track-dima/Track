package it.polimi.dima.track.screens.edit_training

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import it.polimi.dima.track.TRAINING_DEFAULT_ID
import it.polimi.dima.track.common.ext.idFromParameter
import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.service.LogService
import it.polimi.dima.track.model.service.StorageService
import it.polimi.dima.track.screens.TrackViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditTrainingViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
) : TrackViewModel(logService) {
  val training = mutableStateOf(Training())

  fun initialize(trainingId: String) {
    launchCatching {
      if (trainingId != TRAINING_DEFAULT_ID) {
        training.value = storageService.getTraining(trainingId.idFromParameter()) ?: Training()
      }
    }
  }

  fun onTitleChange(newValue: String) {
    training.value = training.value.copy(title = newValue)
  }

  fun onDescriptionChange(newValue: String) {
    training.value = training.value.copy(description = newValue)
  }

  fun onDateChange(newValue: Long) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC))
    calendar.timeInMillis = newValue
    training.value = training.value.copy(
      dueDate = calendar.time,
      dueDateString = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(calendar.time)
    )
  }

  fun onTimeChange(hour: Int, minute: Int) {
    val newDueTime = "${hour.toClockPattern()}:${minute.toClockPattern()}"
    training.value = training.value.copy(
      dueTime = mapOf("hour" to hour, "minute" to minute),
      dueTimeString = newDueTime
    )
  }

  fun onFavoriteToggle(newValue: String) {
    val newFavoriteOption = EditFavouriteOption.getBooleanValue(newValue)
    training.value = training.value.copy(favorite = newFavoriteOption)
  }

  fun onTypeChange(newValue: String) {
    training.value = training.value.copy(type = newValue)
  }

  fun onDoneClick(popUpScreen: () -> Unit) {
    launchCatching {
      val editedTraining = training.value.copy(transient = false)
      if (editedTraining.id.isBlank()) {
        storageService.save(editedTraining)
      } else {
        storageService.update(editedTraining)
      }
      popUpScreen()
    }
  }

  fun onCancelClick(popUpScreen: () -> Unit) {
    if (training.value.transient) {
      launchCatching {
        storageService.delete(training.value.id)
      }
    }
    popUpScreen()
  }

  private fun Int.toClockPattern(): String {
    return if (this < 10) "0$this" else "$this"
  }

  companion object {
    const val UTC = "UTC"
    const val DATE_FORMAT = "EEE, d MMM yyyy"
  }
}
