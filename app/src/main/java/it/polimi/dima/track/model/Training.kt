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
  val url: String = "",
  val favourite: Boolean = false,
  val personalBest: Boolean = false,
  val completed: Boolean = false
)
