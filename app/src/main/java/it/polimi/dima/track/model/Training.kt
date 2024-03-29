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
  val notes: String = "",
  val favorite: Boolean = false,
  val trainingSteps: List<TrainingStep> = listOf(),
  val personalBest: Boolean = false,
  val fitbitData: FitbitData? = null,

  // Searchable tokens
  val searchable: List<String> = listOf(),
) {
  val dueDatetime: Date?
    get() {
      if (dueDate == null || dueTime == null) {
        return null
      }
      return Date(
        dueDate.time
            + (dueTime["hour"] ?: 0) * 60 * 60 * 1000
            + (dueTime["minute"] ?: 0) * 60 * 1000
      )
    }
}
