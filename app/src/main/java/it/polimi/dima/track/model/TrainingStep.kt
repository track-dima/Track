package it.polimi.dima.track.model

data class TrainingStep(
  val id: String = "",
  val type: String = "",
  val durationType: String = "",
  val distance: Int = 0,
  val distanceUnit: String = "m",
  val duration: Int = 0,
  val recover: Boolean = true,
  val recoverType: String = "",
  val recoverDistance: Int = 0,
  val recoverDistanceUnit: String = "m",
  val recoverDuration: Int = 0,
  val extraRecoverType: String = "",
  val extraRecoverDistance: Int = 0,
  val extraRecoverDistanceUnit: String = "m",
  val extraRecoverDuration: Int = 0,
  val repetitions: Int = 0,
  val stepsInRepetition: List<TrainingStep> = listOf(),
  val results: List<String> = listOf()
) {

  fun calculateTree(): Pair<Int, Int> {
    // Calculate the number of normal steps and the number of repetition blocks
    return if (type == Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
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

  fun calculateRepetitions(): Int {
    // Calculate the number of repetitions
    return if (type == Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
      0
    } else if (stepsInRepetition.isEmpty()) {
      1
    } else {
      stepsInRepetition.sumOf { it.calculateRepetitions() } * repetitions
    }
  }

  fun calculateTotalTime(lastInBlock: Boolean): Int {
    // Calculate the total time of the step
    return if (type == Type.REPETITION_BLOCK && stepsInRepetition.isEmpty()) {
      0
    } else if (stepsInRepetition.isEmpty()) {
      val durationTime = if (durationType == DurationType.TIME) {
        duration
      } else {
        distanceToSeconds(distance, distanceUnit)
      }
      val recoverTime = if (type == Type.WARM_UP || type == Type.COOL_DOWN || lastInBlock)
        0
      else if (recoverType == DurationType.TIME)
        recoverDuration
      else
        distanceToSeconds(recoverDistance, recoverDistanceUnit)

      durationTime + recoverTime
    } else {
      val recoverTime = if (recoverType == DurationType.TIME) {
        recoverDuration
      } else {
        distanceToSeconds(recoverDistance, recoverDistanceUnit)
      }
      val extraRecoverTime = if (extraRecoverType == DurationType.TIME) {
        extraRecoverDuration
      } else {
        distanceToSeconds(extraRecoverDistance, extraRecoverDistanceUnit)
      }

      stepsInRepetition.sumOf { it.calculateTotalTime(it.id == stepsInRepetition.last().id) } * repetitions + recoverTime * (repetitions - 1) + extraRecoverTime
    }
  }

  private fun distanceToSeconds(distance: Int, distanceUnit: String): Int {
    // Convert the distance to meters
    val dist = when (distanceUnit) {
      "km" -> distance * 1000
      "mi" -> (distance * 1609.344).toInt()
      else -> distance
    }
    // Convert the distance to seconds (5 min/km)
    return dist * 5 * 60 / 1000
  }

  class DurationType {

    companion object {
      fun getOptions(): List<String> {
        val options = mutableListOf<String>()
        options.add(TIME)
        options.add(DISTANCE)
        return options
      }

      fun getFullOptions(): List<String> {
        val options = mutableListOf<String>()
        options.add(TIME)
        options.add(DISTANCE)
        options.add(NONE)
        return options
      }

      const val TIME = "Time"
      const val DISTANCE = "Distance"
      const val NONE = "None"
    }
  }

  class Type {
    companion object {
      fun getOptions(): List<String> {
        val options = mutableListOf<String>()
        options.add(WARM_UP)
        options.add(COOL_DOWN)
        options.add(REPETITION)
        return options
      }

      const val WARM_UP = "Warm up"
      const val COOL_DOWN = "Cool down"
      const val REPETITION = "Repetition"
      const val REPETITION_BLOCK = "Repetition block"
    }
  }
}
