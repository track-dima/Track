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
    val durationTime = getDurationSeconds()
    val recoverTime =
      if (type !in arrayOf(
          TrainingStep.Type.REPETITION,
          TrainingStep.Type.REPETITION_BLOCK
        ) || lastInBlock
      )
        0
      else getRecoverSeconds()

    durationTime + recoverTime
  } else {
    val recoverTime = getRecoverSeconds()
    val extraRecoverTime = getExtraRecoverSeconds()

    stepsInRepetition.sumOf { it.calculateTotalTime(it.id == stepsInRepetition.last().id) } * repetitions + recoverTime * (repetitions - 1) + extraRecoverTime
  }
}

private fun TrainingStep.getDurationSeconds() =
  if (durationType == TrainingStep.DurationType.TIME) {
    duration
  } else {
    distance.distanceToSeconds(distanceUnit)
  }

private fun TrainingStep.getExtraRecoverSeconds() =
  if (extraRecoverType == TrainingStep.DurationType.TIME) {
    extraRecoverDuration
  } else {
    extraRecoverDistance.distanceToSeconds(extraRecoverDistanceUnit)
  }

private fun TrainingStep.getRecoverSeconds() =
  if (recoverType == TrainingStep.DurationType.TIME) {
    recoverDuration
  } else {
    recoverDistance.distanceToSeconds(recoverDistanceUnit)
  }

fun TrainingStep.parseToString(level: Int, dot: String, lastStep: Boolean): String {
  return when (this.type) {
    TrainingStep.Type.WARM_UP -> "Warm up"
    TrainingStep.Type.COOL_DOWN -> "Cool down"
    TrainingStep.Type.EXERCISES -> "Exercises"
    TrainingStep.Type.STRENGTH -> "Strength exercises"
    TrainingStep.Type.HURDLES -> "Hurdles exercises"

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

fun TrainingStep.calculateSearchTokens(): List<String> {
  val tokens = mutableSetOf<String>()

  return if (type == TrainingStep.Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
    tokens.toList()
  } else if (stepsInRepetition.isEmpty()) {
    tokens.add(getDurationString())
    tokens.toList()
  } else {
    tokens.addAll(stepsInRepetition.flatMap { it.calculateSearchTokens() })
    tokens.toList()
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

private fun TrainingStep.getDurationForResult(): Int =
  if (durationType == TrainingStep.DurationType.TIME) {
    duration
  } else {
    distance.distanceToMeters(distanceUnit)
  }

fun TrainingStep.getBestResults(): Pair<Map<Int, String>, Map<Int, String>> {
  var bestResults = Pair(mutableMapOf<Int, String>(), mutableMapOf<Int, String>())

  return if (type == TrainingStep.Type.REPETITION_BLOCK && stepsInRepetition.isNotEmpty()) {
    stepsInRepetition.forEach {
      bestResults = bestResults.copy(
        first = updateBestTimeResults(bestResults.first, it.getBestResults().first),
        second = updateBestPaceResults(bestResults.second, it.getBestResults().second)
      )
    }
    bestResults
  } else if (type == TrainingStep.Type.REPETITION) {
    if (results.any { it.isNotBlank() && !it.timeIsZero() }) {
      if (durationType == TrainingStep.DurationType.DISTANCE) {
        bestResults.first[getDurationForResult()] = repetitionBestTimeResult()
      } else {
        bestResults.second[getDurationForResult()] = repetitionBestPaceResult()
      }
    }
    bestResults
  } else {
    bestResults
  }
}


fun updateBestTimeResults(
  bestResults: MutableMap<Int, String>,
  newBestResults: Map<Int, String>
): MutableMap<Int, String> {
  newBestResults.forEach { (key, result) ->
    if (bestResults.containsKey(key)) {
      if (result.timeToSeconds() < bestResults[key]!!.timeToSeconds()) {
        bestResults[key] = result
      }
    } else {
      bestResults[key] = result
    }
  }
  return bestResults
}

fun updateBestPaceResults(
  bestResults: MutableMap<Int, String>,
  newBestResults: Map<Int, String>
): MutableMap<Int, String> {
  newBestResults.forEach { (key, result) ->
    if (bestResults.containsKey(key)) {
      if (result.paceToSeconds() < bestResults[key]!!.paceToSeconds()) {
        bestResults[key] = result
      }
    } else {
      bestResults[key] = result
    }
  }
  return bestResults
}

private fun TrainingStep.repetitionBestTimeResult(): String {
  return results.filter { it.isNotBlank() && !it.timeIsZero() }.minBy { it.timeToSeconds() }
}

private fun TrainingStep.repetitionBestPaceResult(): String {
  return results.filter { it.isNotBlank() && !it.timeIsZero() }.minBy { it.paceToSeconds() }
}