package it.polimi.dima.track.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Training(
  @DocumentId val id: String = "",
  val title: String = "",
  val type: String = "",
  val dueDate: Date? = null,
  val dueDateString: String = "",
  val dueTime: Map<String, Int>? = null,
  val dueTimeString: String = "",
  val description: String = "",
  val favorite: Boolean = false,
  val trainingSteps: List<TrainingStep> = listOf(),

  // UNUSED
  val personalBest: Boolean = false,
  val url: String = "",
  val completed: Boolean = false
) {
  fun calculateRepetitions(): Int {
    return trainingSteps.sumOf { it.calculateRepetitions() }
  }

  fun calculateTree(): Pair<Int, Int> {
    if (trainingSteps.isEmpty()) {
      return Pair(0, 0)
    }
    return trainingSteps.map { it.calculateTree() }.reduce { acc, pair ->
      Pair(acc.first + pair.first, acc.second + pair.second)
    }
  }

  fun calculateTotalTime(): Int {
    return trainingSteps.sumOf { it.calculateTotalTime(it.id == trainingSteps.last().id) }
  }
}
