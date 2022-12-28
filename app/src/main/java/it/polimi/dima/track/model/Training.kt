package it.polimi.dima.track.model

import com.google.firebase.firestore.DocumentId

data class Training(
  @DocumentId val id: String = "",
  val title: String = "",
  val priority: String = "",
  val dueDate: String = "",
  val dueTime: String = "",
  val description: String = "",
  val url: String = "",
  val flag: Boolean = false,
  val completed: Boolean = false
)
