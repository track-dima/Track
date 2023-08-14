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
  val parsedTraining: String = "",

  // If true, the training is deleted when the user leaves the screen
  val transient: Boolean = false,

  // UNUSED
  val personalBest: Boolean = false,
  val url: String = "",
  val completed: Boolean = false
)
