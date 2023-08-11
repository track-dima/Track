package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.Training
import it.polimi.dima.track.screens.edit_training.EditTrainingViewModel
import java.util.Calendar
import java.util.TimeZone


fun Training?.hasDueDate(): Boolean {
  return (this?.dueDate != null && this.dueDateString.isNotBlank())
}

fun Training?.hasDueTime(): Boolean {
  return (this?.dueTime != null && this.dueTimeString.isNotBlank())
}

fun Training.isScheduled(): Boolean {
  if (!this.hasDueDate()) {
    return false
  }

  val currentDate = Calendar.getInstance(TimeZone.getTimeZone(EditTrainingViewModel.UTC)).time
  return currentDate.before(this.dueDate)
}

fun Training.getDueDateAndTime(): String {
  val stringBuilder = StringBuilder("")

  if (this.hasDueDate()) {
    stringBuilder.append(this.dueDateString)
    stringBuilder.append(" ")
  }

  if (this.hasDueTime()) {
    stringBuilder.append("at ")
    stringBuilder.append(this.dueTimeString)
  }

  return stringBuilder.toString()
}