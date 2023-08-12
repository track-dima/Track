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

  class PaceUnit {
    companion object {
      fun getOptions(): List<String> {
        val options = mutableListOf<String>()
        options.add(MIN_KM)
        options.add(MIN_MI)
        return options
      }

      const val MIN_KM = "min/km"
      const val MIN_MI = "min/mi"
    }
  }
}
