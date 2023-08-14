package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
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

fun Training.calculateRepetitions(): Int {
  return trainingSteps.sumOf { it.calculateRepetitions() }
}

fun Training.calculateTree(): Pair<Int, Int> {
  if (trainingSteps.isEmpty()) {
    return Pair(0, 0)
  }
  return trainingSteps.map { it.calculateTree() }.reduce { acc, pair ->
    Pair(acc.first + pair.first, acc.second + pair.second)
  }
}

fun Training.calculateTotalTime(): Int {
  return trainingSteps.sumOf { it.calculateTotalTime(it.id == trainingSteps.last().id) }
}

fun Training.parseTrainingSteps(): String {
  val dot = "•"
  return trainingSteps.joinToString(prefix = "$dot ", separator = "\n$dot ") {
    it.parseToString(
      level = 1,
      dot = "◦",
      lastStep = trainingSteps.last().id == it.id
    )
  }
}

fun Training.parseTraining(): String {
  val stringBuilder = StringBuilder(title)

  if (hasDueDate() || hasDueTime()) {
    stringBuilder.append("\n")
    stringBuilder.append(getDueDateAndTime())
  }

  stringBuilder.append("\n\n")
  stringBuilder.append(parseTrainingSteps())

  return stringBuilder.toString()
}

fun emptyResults(trainingSteps: List<TrainingStep>): List<TrainingStep> {
  return trainingSteps.map {
    if (it.stepsInRepetition.isEmpty()) it.copy(results = listOf())
    else it.copy(stepsInRepetition = emptyResults(it.stepsInRepetition))
  }
}