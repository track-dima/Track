package it.polimi.dima.track.model

data class TrainingStep(
  val id: String = "",
  val type: String = "",
  val durationType: String = "",
  val distance: Int = 0,
  val duration: Int = 0,
  val recover: Boolean = true,
  val recoverType: String = "",
  val recoverDistance: Int = 0,
  val recoverDuration: Int = 0,
  val repetitions: Int = 0,
  val stepsInRepetition: List<TrainingStep> = listOf()
) {

  class DurationType {
    companion object {
      const val TIME = "time"
      const val DISTANCE = "distance"
    }
  }

  class Type {
    companion object {
      const val WARM_UP = "warm_up"
      const val COOL_DOWN = "cool_down"
      const val REPETITIONS = "repetitions"
      const val REPETITION_BLOCK = "repetition_block"
    }
  }
}
