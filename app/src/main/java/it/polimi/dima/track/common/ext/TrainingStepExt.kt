package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.TrainingStep

fun TrainingStep.calculateTree(): Pair<Int, Int> {
  // Calculate the number of normal steps and the number of repetition blocks
  return if (type == TrainingStep.Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
    Pair(0, 1)
  } else if (stepsInRepetition.isEmpty()) {
    Pair(1, 0)
  } else {
    val (normalSteps, repetitionBlocks) = stepsInRepetition.map { it.calculateTree() }
      .reduce { acc, pair ->
        Pair(acc.first + pair.first, acc.second + pair.second)
      }
    Pair(normalSteps, repetitionBlocks + 1)
  }
}

fun TrainingStep.calculateRepetitions(): Int {
  // Calculate the number of repetitions
  return if (type == TrainingStep.Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
    0
  } else if (stepsInRepetition.isEmpty()) {
    1
  } else {
    stepsInRepetition.sumOf { it.calculateRepetitions() } * repetitions
  }
}

fun TrainingStep.calculateTotalTime(lastInBlock: Boolean): Int {
  // Calculate the total time of the step
  return if (type == TrainingStep.Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
    0
  } else if (stepsInRepetition.isEmpty()) {
    val durationTime = if (durationType == TrainingStep.DurationType.TIME) {
      duration
    } else {
      distance.distanceToSeconds(distanceUnit)
    }
    val recoverTime =
      if (type == TrainingStep.Type.WARM_UP || type == TrainingStep.Type.COOL_DOWN || lastInBlock)
        0
      else if (recoverType == TrainingStep.DurationType.TIME)
        recoverDuration
      else
        recoverDistance.distanceToSeconds(recoverDistanceUnit)

    durationTime + recoverTime
  } else {
    val recoverTime = if (recoverType == TrainingStep.DurationType.TIME) {
      recoverDuration
    } else {
      recoverDistance.distanceToSeconds(recoverDistanceUnit)
    }
    val extraRecoverTime = if (extraRecoverType == TrainingStep.DurationType.TIME) {
      extraRecoverDuration
    } else {
      extraRecoverDistance.distanceToSeconds(extraRecoverDistanceUnit)
    }

    stepsInRepetition.sumOf { it.calculateTotalTime(it.id == stepsInRepetition.last().id) } * repetitions + recoverTime * (repetitions - 1) + extraRecoverTime
  }
}

fun TrainingStep.parseToString(level: Int, dot: String, lastStep: Boolean): String {
  return when (this.type) {
    TrainingStep.Type.WARM_UP -> "Warm up"
    TrainingStep.Type.COOL_DOWN -> "Cool down"

    TrainingStep.Type.REPETITION -> {
      val duration = getDurationString()
      if (lastStep || !this.recover) duration
      else {
        val recover = getRecoverString()
        "$duration with $recover recovery"
      }
    }

    TrainingStep.Type.REPETITION_BLOCK -> {
      val tabs = "\t" + "\t".repeat(level * 3)
      val recover = getRecoverString()
      val extraRecover = getExtraRecoverString()
      "$repetitions sets of:\n" +
          stepsInRepetition.joinToString(
            prefix = "$tabs$dot ",
            separator = "\n$tabs$dot "
          ) {
            it.parseToString(
              level = level + 1,
              dot = dot,
              lastStep = stepsInRepetition.last().id == it.id
            )
          } +
          " with $recover recovery" +
          "\n$tabs$dot $extraRecover recovery after the sets"
    }

    else -> ""
  }
}

private fun TrainingStep.getExtraRecoverString() =
  if (extraRecoverType == TrainingStep.DurationType.TIME) {
    extraRecoverDuration.formatTimeRecover()
  } else {
    extraRecoverDistance.toString() + extraRecoverDistanceUnit
  }

private fun TrainingStep.getRecoverString() =
  if (recoverType == TrainingStep.DurationType.TIME) {
    recoverDuration.formatTimeRecover()
  } else {
    recoverDistance.toString() + recoverDistanceUnit
  }

private fun TrainingStep.getDurationString() =
  if (durationType == TrainingStep.DurationType.TIME) {
    duration.secondsToHhMmSs()
  } else {
    distance.toString() + distanceUnit
  }